package com.dataminer.pattern.itemset_array_integers_with_count;
import java.util.Arrays;
import java.util.List;

import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.pattern.AbstractOrderedItemset;

import lombok.Setter;

/**
 * This class represents an itemset (a set of items) implemented as an array of integers with
 * a variable to store the support count of the itemset.
 *
 * @author Vasil.Dimitrov^2
 */
public class Itemset extends AbstractOrderedItemset implements Comparable<Itemset> {
	private int[] itemset;
	@Setter
	private int support = 0;

	/**
	 * Constructor used by {@link AlgoRPGrowth}
	 *
	 * @param items
	 */
	public Itemset(int[] items) {
		this.itemset = items;
	}

	/**
	 * Constructor used by {@link AlgoLCM}
	 *
	 * @param itemset
	 * @param support
	 */
	public Itemset(List<Integer> itemset, int support) {
		this.itemset = new int[itemset.size()];
		int i = 0;
		for (Integer item : itemset) {
			this.itemset[i++] = item.intValue();
		}
		this.support = support;
	}

	public void increaseTransactionCount() {
		this.support++;
	}

	@Override
	public int getAbsoluteSupport() {
		return this.support;
	}

	@Override
	public int size() {
		return this.itemset.length;
	}

	@Override
	public Integer get(int position) {
		return this.itemset[position];
	}


	@Override
	public int hashCode() {
		return Arrays.hashCode(this.itemset);
	}

	@Override
	public int compareTo(Itemset o) {
		int x = this.support;
		int y = o.getAbsoluteSupport();

		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}
}
