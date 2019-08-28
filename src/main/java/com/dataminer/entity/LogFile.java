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
public class LogFile {
	private List<UserSession> userSessionList = new ArrayList<>();
	private HashMap<Integer, String> uniqueECMap = new HashMap<>();

	// TODO: This can be optimized by storing the map key value in the nested objects
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
			for (UserEvent event : newUserSession.getUserEventList()) {
				this.userSessionList.get(userSessionIndex).getUserEventList().add(event);
			}
		} else {

			this.userSessionList.add(newUserSession);
		}
	}

	@Override
	public String toString() {
		int limiter = 10;
		StringBuffer strbf = new StringBuffer();
		for (UserSession us : this.userSessionList) {
			strbf.append("Events for user with ip: " + us.getIp() + "\n");
			for (UserEvent ue : us.getUserEventList()) {
				strbf.append("\nTime executed: " + ue.getDateTime());
				strbf.append("\nEvent context: " + this.uniqueECMap.get(ue.getEventContextKey()));
				strbf.append("\n");
			}

			if (limiter-- <= 0) {
				break;
			}
		}
		return strbf.toString();
	}

}
