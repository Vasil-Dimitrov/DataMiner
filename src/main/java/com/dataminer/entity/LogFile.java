package com.dataminer.entity;

import java.util.ArrayList;
import java.util.List;

import com.dataminer.constant.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogFile {
	private List<UserSession> userSessionList = new ArrayList<>();

	public void addLine(String line) {
		String elements[] = line.split("\t");
		if (elements.length != Constant.LOG_FILE_VALID_SIZE) {
			return;
		}

		UserSession newUserSession = new UserSession(elements);
		if (this.userSessionList.contains(newUserSession)) {
			int usIndex = this.userSessionList.indexOf(newUserSession);
			for (UserEvent event : newUserSession.getUserEventList()) {
				this.userSessionList.get(usIndex).getUserEventList().add(event);
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
				strbf.append("\nEvent context: " + ue.getEventContext());
				strbf.append("\n");
			}

			if (limiter-- <= 0) {
				break;
			}
		}
		return strbf.toString();
	}

}
