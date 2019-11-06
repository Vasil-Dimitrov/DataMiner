package com.dataminer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataminer.constant.Constant;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.repository.AlgoSettingsRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class that implements Repository methods for working with {@link AlgoSettings}
 * entity
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
@Service
@Transactional(readOnly = false)
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
		AlgoSettings result = null;
		try {
			result = this.repository.save(algoSettings);
		} catch (Exception e) {
			log.error("Exception thrown trying to save algoSettings: \n" + algoSettings.toString(), e);
		}
		return result != null;
	}

}
