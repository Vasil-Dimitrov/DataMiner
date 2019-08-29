package com.dataminer.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dataminer.constant.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//TODO: add logger
public class LogFile {
	private List<UserSession> userSessionList = new ArrayList<>();
	private HashMap<Integer, String> uniqueECMap = new HashMap<>();

	public void addLine(String line) {
		String elements[] = line.split("\t");
		if (elements.length != Constant.LOG_FILE_VALID_SIZE) {
			return;
		}
		RawLineElements rawLine = new RawLineElements(elements);

		// adding to the uniqueEventContextMap
		if (!this.uniqueECMap.containsValue(rawLine.getEventContext())) {
			this.uniqueECMap.put((this.uniqueECMap.size() + 1), rawLine.getEventContext());
		}

		UserSession newUserSession;
		try {
			newUserSession = new UserSession(rawLine.getIp(),
					LocalDateTime.parse(rawLine.getDateTime(), Constant.LOG_FILE_DATE_TIME_FORMAT),
					Integer.valueOf(this.uniqueECMap.size()));
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
		int limiter = 10;
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
