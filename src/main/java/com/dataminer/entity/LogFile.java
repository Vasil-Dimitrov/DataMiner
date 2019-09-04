package com.dataminer.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataminer.constant.Constant;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//TODO: add logger
public class LogFile {
	private List<UserSession> userSessionList = new ArrayList<>();
	private BiMap<Integer, String> uniqueECMap = HashBiMap.create();
	private Map<Integer, Integer> keyCount = new HashMap<>();

	public void addLine(String line) {
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
					LocalDateTime.parse(rawLine.getDateTime(), Constant.LOG_FILE_DATE_TIME_FORMAT),
					lastKey != null ? lastKey : this.uniqueECMap.inverse().get(rawLine.getEventContext()));
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return;
		}

		if (this.userSessionList.contains(newUserSession)) {
			int userSessionIndex = this.userSessionList.indexOf(newUserSession);
			this.userSessionList.get(userSessionIndex).addUserSessionEvent(newUserSession);
		} else {

			this.userSessionList.add(newUserSession);
		}
	}

	/*
	 *
	 * (non-Javadoc) This toString has been implemented with a limiter as the object itself is
	 * too big
	 *
	 * @see java.lang.Object#toString()
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
