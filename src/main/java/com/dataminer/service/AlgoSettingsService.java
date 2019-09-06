package com.dataminer.service;

import com.dataminer.pojo.entity.AlgoSettings;

public interface AlgoSettingsService {
	AlgoSettings getDefaultAlgoSettings();

	boolean saveAlgoSettings(AlgoSettings algoSettings);
}
