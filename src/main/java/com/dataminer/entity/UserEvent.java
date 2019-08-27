package com.dataminer.entity;

import java.time.LocalDateTime;

import com.dataminer.constant.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEvent {

	LocalDateTime dateTime;

	String eventContext;

	/*- not needed for the moment so commented out to preserve memory
	String component;

	String eventName;

	String description;
	 */

	public UserEvent(String[] line) {
		this.dateTime = LocalDateTime.parse(line[Constant.LOG_FILE_DATE_TIME_POSITION], Constant.LOG_FILE_DATE_TIME_FORMAT);
		this.eventContext = line[Constant.LOG_FILE_EVENT_CONTEXT_POSITION];
	}

}
