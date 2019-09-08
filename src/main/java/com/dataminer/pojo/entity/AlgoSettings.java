package com.dataminer.pojo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.dataminer.constant.Constant;

import lombok.Data;

/**
 * Class representation of DB table algo_settings
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Data
@Entity(name = "algo_settings")
public class AlgoSettings {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false, nullable = false, unique = true)
	private Integer ID;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean lcm;
	@Column(nullable = false)
	private Double lcmMinSup;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean rpg;
	@Column(nullable = false)
	private Double rpgMinSup;
	@Column(nullable = false)
	private Double rpgMinRareSup;

	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean vtsa;
	@Column(nullable = false)
	private Integer vtsaHourInterval;

	/**
	 * Method for checking if all the algorithms are disabled
	 *
	 * @return
	 */
	public boolean areAllAlgorithmsDisabled() {
		return !(this.lcm || this.rpg || this.vtsa);
	}
	
	/**
	 * Method for validating the data of the object. Returns a list with all the errors.
	 * 
	 * @return
	 */
	public List<String> isValid() {
		List<String> errors = new ArrayList<>();
		
		if(lcm == null)
			lcm = false;
		if(rpg == null)
			rpg = false;
		if(vtsa == null)
			vtsa = false;

		if(lcmMinSup == null || lcmMinSup <= 0 || lcmMinSup >= 1)
			errors.add("Minimum Support за Най-често достъпвани евенти: " + Constant.INVALID_SUPPORT_VALUE);
		if(rpgMinSup == null || rpgMinSup <= 0 || rpgMinSup >= 1)
			errors.add("Minimum Support за Рядко достъпвани евенти: " + Constant.INVALID_SUPPORT_VALUE);
		if(rpgMinRareSup == null || rpgMinRareSup <= 0 || rpgMinRareSup >= 1)
			errors.add("Minimum Rare Support за Рядко достъпвани евенти: " + Constant.INVALID_SUPPORT_VALUE);
		if(rpgMinRareSup != null && rpgMinSup != null && rpgMinRareSup >= rpgMinSup)
			errors.add("Рядко достъпвани евенти:: Minimum Rare Support трябва да бъде по-малка стойност от Minimum Support");
		if(vtsaHourInterval == null || vtsaHourInterval < 4 || vtsaHourInterval > 24 || 24 % vtsaHourInterval != 0)
			errors.add("Hour Interval: Невалидна въведена стойност! Позволени стойности: 4 <= Х <= 24 и 24/х трябва да дава цяло число.");
		
		return errors;
	}

	@Override
	public String toString() {
		return String.format(
				"id: %s \nlcm: %s \nlcmMinSup: %s \n\nrpg: %s \nrpgMinSup: %s \nrpgMinRareSup: %s \n\nvtsa: %s \nvtsaHourInterval: %s \n",
				this.ID, this.lcm,
				this.lcmMinSup, this.rpg, this.rpgMinSup, this.rpgMinRareSup, this.vtsa, this.vtsaHourInterval);
	}
}
