package com.dataminer.algorithm.vtsa;

import java.time.DateTimeException;
import java.time.DayOfWeek;

import lombok.Getter;

/**
 * Enumeration for Bulgarian short week day names
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Getter
public enum DayOfWeekBG {
	MONDAY("По"),
	TUESDAY("Вт"),
	WEDNESDAY("Ср"),
	THURSDAY("Че"),
	FRIDAY("Пе"),
	SATURDAY("Съ"),
	SUNDAY("Не");

	private String shortName;


	private static final DayOfWeekBG[] ENUMS = DayOfWeekBG.values();

	private DayOfWeekBG(String shortName) {
		this.shortName = shortName;
	}

	public static DayOfWeekBG of(int dayOfWeek) {
		if ((dayOfWeek < 1) || (dayOfWeek > 7)) {
			throw new DateTimeException("Invalid value for DayOfWeek: " + dayOfWeek);
		}
		return ENUMS[dayOfWeek - 1];
	}

	public static DayOfWeekBG of(DayOfWeek dayOfWeekEnum) {
		int dayOfWeek = dayOfWeekEnum.getValue();
		return ENUMS[dayOfWeek - 1];
	}
}
