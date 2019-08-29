package com.dataminer.algorithm.lcm;

import java.util.List;

/**
 * This class has an abstraction of a dataset transaction.
 */
public class Transaction {
	public static Integer[] temp = new Integer[500];

	Transaction originalTransaction;
	int offset;

	private Integer[] items;

	public Transaction(Integer[] items) {
		this.originalTransaction = this;
		this.items = items;
		this.offset = 0;
	}

	public Transaction(Transaction transaction, int offset) {
		this.originalTransaction = transaction.originalTransaction;

		this.items = transaction.getItems();
		this.offset = offset;
	}

	public Integer[] getItems() {
		return this.items;
	}

	/**
	 * Check if an item appears in this transaction
	 * @param item  the item
	 * @return true if it appears. Otherwise, false.
	 */
	public int containsByBinarySearch(Integer item) {
		//		if(item > items[items.length -1]) {
		//			return -1;
		//		}
		int low = this.offset;
		int high = this.items.length - 1;

		while (high >= low) {
			int middle = ( low + high ) >>> 1; // divide by 2
			if (this.items[middle].equals(item)) {
				return middle;
			}
			if (this.items[middle]< item) {
				low = middle + 1;
			}
			if (this.items[middle] > item) {
				high = middle - 1;
			}
		}
		return -1;
	}

	public boolean containsByBinarySearchOriginalTransaction(Integer item) {
		Integer[] originalItems = this.originalTransaction.getItems();
		int low = 0;
		int high = originalItems.length - 1;

		while (high >= low) {
			int middle = ( low + high ) >>> 1; // divide by 2
			if (originalItems[middle].equals(item)) {
				return true;
			}
			if (originalItems[middle]< item) {
				low = middle + 1;
			}
			if (originalItems[middle] > item) {
				high = middle - 1;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		for(Integer item : this.items) {
			string.append(item.intValue());
			string.append(" ");
		}
		return string.toString();
	}

	public void removeInfrequentItems(List<Transaction>[] buckets, int minsupRelative) {

		// copy only the frequent itemsets after the offset in the temporary buffer
		int i = 0;
		for(Integer item : this.items) {
			if(buckets[item].size() >= minsupRelative) {
				temp[i++] = item;
			}
		}
		// copy the buffer back into the original array
		this.items = new Integer[i];
		System.arraycopy(temp, 0, this.items, 0, i);
	}


}
