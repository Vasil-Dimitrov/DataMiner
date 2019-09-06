package com.dataminer.algorithm.vtsa;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Week {
	List<DayFraction> dayFraction = new ArrayList<>();


	/**
	 * Validates the hour interval. The hour interval needs to be bigger than 2, smaller than
	 * 24 and 24 should be divided by it without leaving a fraction
	 *
	 * @param hourInterval
	 * @return
	 */
	public static boolean isHourIntervalValid(Integer hourInterval) {
		if ((hourInterval == null) || (hourInterval < 2) || (hourInterval > 24) || ((24 % hourInterval) != 0)) {
			return false;
		}

		return true;
	}

	/**
	 * Creates a Week with DayFraction objects based on the hour interval passed. Make sure to
	 * call Week.isHourIntervalValid(hourInterval) first to avoid any errors. The validation
	 * has been excluded from this method for performance.
	 *
	 * @param hourInterval
	 * @return
	 */
	public static Week getWeekWithHourInterval(Integer hourInterval) {
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

		return new Week(dayFraction);
	}
}
