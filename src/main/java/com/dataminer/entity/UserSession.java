package com.dataminer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {

	private String ip;

	// private List<UserEvent> userEventList = new ArrayList<>();

	private List<LocalDateTime> dateTimeList = new ArrayList<>();

	private SortedSet<Integer> eventContextKeySet = new TreeSet<>();

	/*- not needed for the moment so commented out to preserve memory
	List<String> component;

	List<String> eventName;

	List<String> description;
	 */

	public UserSession(String ip, LocalDateTime dateTime, Integer eventContextKey) {
		this.ip = ip;
		this.dateTimeList.add(dateTime);
		this.eventContextKeySet.add(eventContextKey);
	}

	public void addUserSessionEvent(UserSession userSession) {
		if (!(CollectionUtils.isEmpty(userSession.getDateTimeList())) && !(CollectionUtils.isEmpty(userSession.getEventContextKeySet()))) {
			this.dateTimeList.add(userSession.getDateTimeList().get(0));
			this.eventContextKeySet.add(userSession.getEventContextKeySet().first());
		}
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
