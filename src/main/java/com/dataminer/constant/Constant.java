package com.dataminer.constant;

import java.time.format.DateTimeFormatter;

/**
 * Class for defining common constants
 *
 * @author Vasil.Dimitrov^2
 *
 */
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

	public static final String LCM_TITLE_TEXT = "Често достъпвани евенти";
	public static final String RPG_TITLE_TEXT = "Рядко достъпвани евенти";

	public static final String debugFile = "contextPasquier99.txt";
	//	public static final String debugReadFile = "logs_BCS37_20181103_UTF-8.csv";
	public static final String debugReadFile = "logs_BCS37_20181103_UTF-8-small-bg.csv";

	public static final int DEFAULT_ALGO_SETTINGS_ID = 1;
	
	public static final String INVALID_SUPPORT_VALUE = "Невалидна въведена стойност! Позволени стойности: 0 < Х < 1";

	public static final String FILE_UPLOAD_DIR = System.getProperty("user.dir") + "\\upload_files\\" + "analysis_file.txt";

	public static final String NO_FILE_ERROR = "Лог файлът не бе намерен.";
	public static final String BAD_FILE_ERROR = "Възникна грешка по време на обработка на файла.";
}
