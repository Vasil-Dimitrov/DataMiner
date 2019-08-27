package com.dataminer.entity;

import java.util.ArrayList;
import java.util.List;

import com.dataminer.constant.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {

	private String ip;

	private List<UserEvent> userEventList = new ArrayList<>();

	public UserSession(String userEventData[]) {
		this.ip = userEventData[Constant.LOG_FILE_IP_POSITION];
		this.userEventList.add(new UserEvent(userEventData));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 3;
		result = (prime * result) + ((this.ip == null) ? 0 : this.ip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserSession other = (UserSession) obj;
		if (this.ip == null) {
			if (other.ip != null) {
				return false;
			}
		} else if (!this.ip.equals(other.ip)) {
			return false;
		}
		return true;
	}
}
