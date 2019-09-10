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
}
