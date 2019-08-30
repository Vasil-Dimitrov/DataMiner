package com.dataminer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class used for storing mock data for the ui while the backend is still being developed
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class MockUserEvent {
	String eventContext;

	Integer percent;
}
