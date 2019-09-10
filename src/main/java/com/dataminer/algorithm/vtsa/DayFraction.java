package com.dataminer.algorithm.vtsa;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayFraction {
	private int dayOfWeek;
	private int occurrence;

	private LocalTime fromTime;
	private LocalTime toTime;

	public DayFraction(int dayOfWeek, int fromHour, int toHour) {
		this.dayOfWeek = dayOfWeek;
		this.fromTime = LocalTime.of(fromHour, 0);
		this.toTime = LocalTime.of(toHour - 1, 59);
	}
	
	public String getDayOfWeekBG() {
		return  DayOfWeekBG.of(this.dayOfWeek).getShortName();
	}
	
	public String getTimePeriod() {
		return String.format("%s-%s", this.fromTime, this.toTime);
	}
	
	public String getDisplayName() {
		return String.format("%s %s", getDayOfWeekBG(), getTimePeriod());
	}
	
	public void addOccurremce() {
		this.occurrence++;
	}
}
