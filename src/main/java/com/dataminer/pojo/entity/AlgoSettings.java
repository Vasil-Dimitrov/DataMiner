package com.dataminer.pojo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

	@Override
	public String toString() {
		return String.format(
				"id: %s \nlcm: %s \nlcmMinSup: %s \n\nrpg: %s \nrpgMinSup: %s \nrpgMinRareSup: %s \n\nvtsa: %s \nvtsaHourInterval: %s \n",
				this.ID, this.lcm,
				this.lcmMinSup, this.rpg, this.rpgMinSup, this.rpgMinRareSup, this.vtsa, this.vtsaHourInterval);
	}
}
