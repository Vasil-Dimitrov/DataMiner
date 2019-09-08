package com.dataminer.pojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.dataminer.constant.Constant;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Pojo class for storing the log data in an ordered and easy to analyze structure.
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Getter
@Setter
@Slf4j
public class LogFile {
	private List<UserSession> userSessionList = new ArrayList<>();
	private BiMap<Integer, String> uniqueECMap = HashBiMap.create();
	private Map<Integer, Integer> keyCount = new HashMap<>();

	/**
	 * Method for adding a log line to the {@link LogFile} object
	 *
	 * @param line
	 */
	public void addLine(String line, boolean isVtsaOn) {
		String elements[] = line.split("\t");
		Integer lastKey = null;
		if (elements.length != Constant.LOG_FILE_VALID_SIZE) {
			return;
		}

		RawLineElements rawLine = new RawLineElements(elements);

		// adding to the uniqueEventContextMap
		if (!this.uniqueECMap.containsValue(rawLine.getEventContext())) {
			lastKey = this.uniqueECMap.size() + 1;
			this.uniqueECMap.put((lastKey), rawLine.getEventContext());
			this.keyCount.put(lastKey, 1);
		} else {
			lastKey = this.uniqueECMap.inverse().get(rawLine.getEventContext());
			this.keyCount.put(lastKey, this.keyCount.get(lastKey) + 1);
		}

		UserSession newUserSession;
		try {
			newUserSession = new UserSession(rawLine.getIp(),
					isVtsaOn ? LocalDateTime.parse(rawLine.getDateTime(), Constant.LOG_FILE_DATE_TIME_FORMAT) : null,
							lastKey != null ? lastKey : this.uniqueECMap.inverse().get(rawLine.getEventContext()));
		} catch (DateTimeParseException e) {
			log.error("DateTimeParseException was thrown for " + rawLine.getDateTime(), e);
			return;
		}

		if (this.userSessionList.contains(newUserSession)) {
			int userSessionIndex = this.userSessionList.indexOf(newUserSession);
			this.userSessionList.get(userSessionIndex).addUserSessionEvent(newUserSession);
		} else {
			this.userSessionList.add(newUserSession);
		}
	}


	/**
	 * Create a LogFile out of a MultipartFile. Supports Cyrillic encoding.
	 *
	 * @param mFile
	 * @return
	 */
	public static LogFile createFromMultipartFile(MultipartFile mFile, boolean isVtsaOn) {

		LogFile logFile = new LogFile();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(mFile.getInputStream(), StandardCharsets.ISO_8859_1))) {
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				logFile.addLine(new String(line.getBytes("Cp1252"), "Cp1251"), isVtsaOn);
			}
		} catch (IOException e) {
			log.error("Error creating LogFile from file " + mFile.getOriginalFilename(), e);
		}

		return logFile;
	}

	/*
	 * This toString has been implemented with a limiter as the object itself is too big
	 */
	@Override
	public String toString() {
		int limiter = 1;
		StringBuffer strbf = new StringBuffer();
		for (UserSession us : this.userSessionList) {
			strbf.append("Events for user with ip: " + us.getIp() + "\n");
			int i = 0;
			for (Integer eventContextKey : us.getEventContextKeySet()) {
				strbf.append("\nTime executed: " + us.getDateTimeList().get(i++));
				strbf.append("\nEvent context: " + this.uniqueECMap.get(eventContextKey));
				strbf.append("\n");
			}

			if (limiter-- <= 0) {
				break;
			}
		}
		return strbf.toString();
	}

}
