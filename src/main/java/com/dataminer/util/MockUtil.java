package com.dataminer.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MockUtil {
	public static Map<String, Integer> getMockUserEventList() {
		Map<String, Integer> list = new LinkedHashMap<>();
		list.put("Course: Програмни езици", 92);
		list.put("Course: Програмни езици, File: Пример", 60);
		list.put("Course: Програмни езици, File: Упражнение 1", 55);
		list.put("Course: Програмни езици, File: Лекция 1, File: Упражнение 1", 51);
		return list;
	}

	public static Map<String, Integer> getMockRareUserEventList() {
		Map<String, Integer> list = new LinkedHashMap<>();
		list.put(
				"Course: Програмни езици, File: Конспект 2014-2015, File: Generics (генетични типове) .Lambda expressions в C# и C++. Паралелни конструктори в С++",
				5);
		list.put("File: Лекция 10, File: Software contractions", 10);
		list.put("File: Проект Weather Station, Course: Програмни езици", 16);
		return list;
	}

	public static Map<String, Integer> getMockTimeSomething() {
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put("По 0:00-6:00", 15);
		map.put("По 6:00-12:00", 20);
		map.put("По 12:00-18:00", 14);
		map.put("По 18:00-24:00", 10);

		map.put("Вт 0:00-6:00", 0);
		map.put("Вт 6:00-12:00", 5);
		map.put("Вт 12:00-18:00", 8);
		map.put("Вт 18:00-24:00", 12);

		map.put("Ср 0:00-6:00", 1);
		map.put("Ср 6:00-12:00", 7);
		map.put("Ср 12:00-18:00", 8);
		map.put("Ср 18:00-24:00", 10);

		map.put("Че 0:00-6:00", 0);
		map.put("Че 6:00-12:00", 5);
		map.put("Че 12:00-18:00", 7);
		map.put("Че 18:00-24:00", 6);

		map.put("Пе 0:00-6:00", 0);
		map.put("Пе 6:00-12:00", 4);
		map.put("Пе 12:00-18:00", 6);
		map.put("Пе 18:00-24:00", 5);

		map.put("Съ 0:00-6:00", 0);
		map.put("Съ 6:00-12:00", 2);
		map.put("Съ 12:00-18:00", 5);
		map.put("Съ 18:00-24:00", 3);

		map.put("Не 0:00-6:00", 0);
		map.put("Не 6:00-12:00", 15);
		map.put("Не 12:00-18:00", 20);
		map.put("Не 18:00-24:00", 28);

		return map;
	}

	public static Integer getMockTimeSomethingMaxValue() {
		int maxNum = 0;
		Map<String, Integer> map = getMockTimeSomething();
		for (Integer value : map.values()) {
			if (maxNum < value) {
				maxNum = value;
			}
		}
		return maxNum;
	}

}
