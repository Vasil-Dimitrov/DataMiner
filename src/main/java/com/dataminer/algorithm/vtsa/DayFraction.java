package com.dataminer.algorithm.vtsa;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayFraction {
	Integer dayOfWeek;
	Integer count;

	LocalTime fromTime;
	LocalTime toTime;

	public DayFraction(int dayOfWeek, int fromHour, int toHour) {
		this.dayOfWeek = dayOfWeek;
		this.count = 0;
		this.fromTime = LocalTime.of(fromHour, 0);
		this.toTime = LocalTime.of(toHour, 0);
	}

	public String getDisplayName() {
		return String.format("%s %s-%s", DayOfWeekBG.of(this.dayOfWeek), this.fromTime, this.toTime);
	}
}
