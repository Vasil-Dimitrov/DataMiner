package com.dataminer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dataminer.constant.Constant;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.repository.AlgoSettingsRepository;

@Service
public class AlgoSettingsServiceImpl implements AlgoSettingsService {
	private final AlgoSettingsRepository repository;

	@Autowired
	public AlgoSettingsServiceImpl(AlgoSettingsRepository repository) {
		this.repository = repository;
	}

	@Override
	public AlgoSettings getDefaultAlgoSettings() {
		return this.repository.findById(Constant.DEFAULT_ALGO_SETTINGS_ID).orElse(null);
	}

	@Override
	public boolean saveAlgoSettings(AlgoSettings algoSettings) {
		return this.repository.save(algoSettings) != null;
	}


}
