package com.dataminer.algorithm.pattern;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dataminer.pojo.LogFile;
import com.dataminer.util.HelperUtil;

import lombok.Getter;

/**
 * This class hold a list of a list of itemsets. Where the first level of the list
 * corresponds to the size of the itemsets contained inside it.
 *
 * @author Vasil.Dimitrov^2
 *
 */
public class Itemsets{
	private final List<List<Itemset>> levels = new ArrayList<>();
	@Getter
	private int itemsetsCount = 0;
	private String name;

	public Itemsets(String name) {
		this.name = name;
		// adding empty level 0 by default
		this.levels.add(new ArrayList<Itemset>());
	}

	public void printItemsets() {
		System.out.println(" ------- " + this.name + " -------");
		int patternCount = 0;
		int levelCount = 0;
		// for each level (a level is a set of itemsets having the same number of items)
		for (List<Itemset> level : this.levels) {
			// print how many items are contained in this level
			System.out.println("  L" + levelCount + " ");
			// for each itemset
			for (Itemset itemset : level) {
				//				Arrays.sort(itemset.getItems());
				// print the itemset
				System.out.print("  pattern " + patternCount + ":  ");
				itemset.print();
				// print the support of this itemset
				System.out.print("support :  " + itemset.getAbsoluteSupport());
				patternCount++;
				System.out.println("");
			}
			levelCount++;
		}
		System.out.println(" --------------------------------");
	}


	public Map<String, Double> getRelativeItemsets(LogFile logFile) {
		Map<String, Double> map = new LinkedHashMap<>();
		int totalTransactionCount = logFile.getUserSessionList().size();

		for (List<Itemset> level : this.levels) {
			for (Itemset itemset : level) {
				String eventSet = logFile.getUniqueECMap().get(itemset.getItemset()[0]);
				for (int i = 1; i < itemset.getItemset().length; i++) {
					eventSet += ", " + logFile.getUniqueECMap().get(itemset.getItemset()[i]);
				}
				double percentage = (double) (100 * itemset.getAbsoluteSupport()) / totalTransactionCount;
				map.put(eventSet, HelperUtil.round(percentage, 2));
			}
		}
		return map;
	}

	/**
	 * Sort the itemsets on the second level. The sort order is chosen based on the passed
	 * argument.
	 *
	 * @param reverseOrder
	 */
	public void sortItemsets(boolean reverseOrder) {
		if (reverseOrder) {
			for (List<Itemset> level : this.levels) {
				Collections.sort(level, Collections.reverseOrder());
			}
		} else {
			for (List<Itemset> level : this.levels) {
				Collections.sort(level);
			}
		}
	}
	
	/**
	 * Method returning the analysis in a formatted string for saving onto a file
	 * 
	 * @return
	 */
	public String getAnalysisForFile(LogFile logFile) {
		StringBuilder str = new StringBuilder();
		str.append("======= " + name + " ======= \n");
		str.append("#		|		Евент		|		Достъпваност спрямо потребители\n");

		int i = 1;
		for (Map.Entry<String, Double> entry : getRelativeItemsets(logFile).entrySet()) {
			str.append(String.format("%s		|		%s		|		%s%%\n", i, entry.getKey(), entry.getValue()));
			i++;
		}

		return str.toString();
	}

	public void addItemset(Itemset itemset, int k) {
		while (this.levels.size() <= k) {
			this.levels.add(new ArrayList<Itemset>());
		}
		this.levels.get(k).add(itemset);
		this.itemsetsCount++;
	}

	public void decreaseItemsetCount() {
		this.itemsetsCount--;
	}
}
