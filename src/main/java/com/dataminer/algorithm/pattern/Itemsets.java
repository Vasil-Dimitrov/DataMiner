package com.dataminer.algorithm.pattern;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	/**
	 * Sort the itemsets on the second level based on their support in descending order
	 */
	public void sortItemsets() {
		for (List<Itemset> level : this.levels) {
			Collections.sort(level, Collections.reverseOrder());
		}
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
