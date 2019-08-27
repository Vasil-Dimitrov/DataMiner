package com.dataminer.constant;

import java.time.format.DateTimeFormatter;

public class Constant {

	private Constant() {
	}

	public static final DateTimeFormatter LOG_FILE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("\"d/MM/yy, HH:mm\"");

	public static final int LOG_FILE_DATE_TIME_POSITION = 0;
	public static final int LOG_FILE_EVENT_CONTEXT_POSITION = 1;
	public static final int LOG_FILE_COMPONENT_POSITION = 2;
	public static final int LOG_FILE_EVENT_NAME_POSITION = 3;
	public static final int LOG_FILE_DESCRIPTION_POSITION = 4;
	public static final int LOG_FILE_ORIGIN_POSITION = 5;
	public static final int LOG_FILE_IP_POSITION = 6;
	public static final int LOG_FILE_VALID_SIZE = 7;
}
