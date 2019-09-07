package com.dataminer.repository;

import org.springframework.data.repository.CrudRepository;

import com.dataminer.pojo.entity.AlgoSettings;

/**
 * Repository class for the {@link AlgoSettings} entity
 *
 * @author Vasil.Dimitrov^2
 *
 */
public interface AlgoSettingsRepository extends CrudRepository<AlgoSettings, Integer> {
}

