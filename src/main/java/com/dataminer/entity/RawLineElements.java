package com.dataminer.entity;

import com.dataminer.constant.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawLineElements {
	private String dateTime;
	private String eventContext;
	private String component;
	private String eventName;
	private String description;
	private String origin;
	private String ip;

	public RawLineElements(String[] elements) {
		if (elements.length == Constant.LOG_FILE_VALID_SIZE) {
			this.dateTime = elements[Constant.LOG_FILE_DATE_TIME_POSITION];
			this.eventContext = elements[Constant.LOG_FILE_EVENT_CONTEXT_POSITION];
			this.component = elements[Constant.LOG_FILE_COMPONENT_POSITION];
			this.eventName = elements[Constant.LOG_FILE_EVENT_NAME_POSITION];
			this.description = elements[Constant.LOG_FILE_DESCRIPTION_POSITION];
			this.origin = elements[Constant.LOG_FILE_ORIGIN_POSITION];
			this.ip = elements[Constant.LOG_FILE_IP_POSITION];
		}
	}
}
