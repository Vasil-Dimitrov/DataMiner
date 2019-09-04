package com.dataminer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlgoSettings {
	boolean lcm;
	double lcmMinSup;
	boolean rpg;
	double rpgMinSup;
	double rpgMinRareSup;
	boolean vtsa;
}
