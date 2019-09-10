package com.dataminer.algorithm.vtsa;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.UserSession;
import com.dataminer.util.HelperUtil;

import lombok.Getter;

public class AlgoVTSA {
	private long startTime;
	private long endTime;
	
	private Integer timeInterval;
	private Week week;
	@Getter
	private double maxValue;
	
	public void run(LogFile logFile, Integer interval) {
		this.startTime = System.currentTimeMillis();
		// check if timeInterval is valid and != null
		this.timeInterval = interval;
		getWeekWithHourInterval(timeInterval);

		for (UserSession userSession : logFile.getUserSessionList()) {
			for (LocalDateTime transactionTime : userSession.getDateTimeList()) {
				int dayOfWeek = transactionTime.getDayOfWeek().getValue();
				LocalTime timeOfDay = transactionTime.toLocalTime();

				addFractionOccurrence(week, dayOfWeek, timeOfDay);
			}
		}
		
		this.endTime = System.currentTimeMillis();
		System.out.println("Time it took for VTSA: " + (endTime - startTime) + " ms");
	}

	/**
	 * Creates a Week with DayFraction objects based on the hour interval passed. Make sure to
	 * call Week.isHourIntervalValid(hourInterval) first to avoid any errors. The validation
	 * has been excluded from this method for performance.
	 *
	 * @param hourInterval
	 * @return
	 */
	private void getWeekWithHourInterval(Integer hourInterval) {
		int dayPerWeek = 7;
		int intervalsPerDay = 24 / hourInterval;
		List<DayFraction> dayFraction = new ArrayList<>();

		for (int day = 1; day <= dayPerWeek; day++) {
			for (int interval = 1; interval <= intervalsPerDay; interval++) {
				int fromHour = (interval - 1) * hourInterval;
				int toHour = interval * hourInterval;
				dayFraction.add(new DayFraction(day, fromHour, toHour));
			}
		}

		this.week = new Week(dayFraction);
	}

	public void addFractionOccurrence(Week week, int dayOfWeek, LocalTime timeOfDay) {
		for (DayFraction dayFraction : week.getDayFraction()) {
			if ((dayFraction.getDayOfWeek() == dayOfWeek)
					&& (dayFraction.getFromTime().equals(timeOfDay) || dayFraction.getFromTime().isBefore(timeOfDay))
					&& (dayFraction.getToTime().equals(timeOfDay) || dayFraction.getToTime().isAfter(timeOfDay))) {
				dayFraction.addOccurremce();
				break;
			}
		}
	}

	public Map<String, Double> getResults(int transactionsCount) {
		Map<String, Double> map = new LinkedHashMap<>();
		
		double percentageRnd;
		for (DayFraction fraction : week.getDayFraction()) {
			percentageRnd = HelperUtil.round(((double) (100 * fraction.getOccurrence()) / transactionsCount), 2);
			map.put(fraction.getDisplayName(), HelperUtil.round(percentageRnd, 2));
			
			if(percentageRnd > this.maxValue) {
				maxValue = percentageRnd;
			}
		}
		
		return map;
	}

	public String getFileStringData(int transactionsCount) {
		StringBuilder str = new StringBuilder();
		str.append("======= Времеви трафик ======= \n");
		str.append("Ден от седмицата		|		Времеви период		|		Достъпваност спрямо общо потребление \n");

		for (DayFraction fraction : week.getDayFraction()) {
			str.append(String.format("%s		|		%s		|		%s%%\n", fraction.getDayOfWeek(), fraction.getTimePeriod(),
					HelperUtil.round(((double) (100 * fraction.getOccurrence()) / transactionsCount), 2)));
		}
		str.append(String.format("Време за анализ: %s ms \n\n", this.endTime - this.startTime));
		return str.toString();
	}
}








