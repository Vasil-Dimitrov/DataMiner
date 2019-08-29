package com.dataminer.algorithm.lcm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dataminer.pattern.itemset_array_integers_with_count.Itemset;
import com.dataminer.pattern.itemset_array_integers_with_count.Itemsets;

/**
 * This is an implementation of the LCM algorithm for
 * mining frequent closed itemsets from a transaction database.
 *
 * The implementation is similar to LCM version 2 with some differences.
 * For example, transaction merging is not performed yet and
 * items in transactions are not sorted in descending order of frequency.
 *
 */
public class AlgoLCM {

	private Itemsets closedFrequentItemsets;

	// object to write the output file
	BufferedWriter writer = null;

	// the number of frequent itemsets found (for
	// statistics)
	private int frequentCount;

	// the start time and end time of the last algorithm execution
	long startTimestamp;
	long endTimestamp;
	int minsupRelative;

	// Buckets for occurence delivery
	// Recall that each bucket correspond to an item
	// and contains the transactions where the items appears.
	private List<Transaction>[] buckets;

	public AlgoLCM() {

	}

	/**
	 * Run the algorithm
	 * @param minimumSupport  the minimum support threshold as percentage value between 0 and 1
	 * @param dataset  the dataset
	 * @param outputPath  the output file path to save the result or null if to be kept in memory
	 * @param mineAllFrequentItemsets mine all frequent itemsets
	 * @param mineAllMaximalItemsets mine only maximal itemsets
	 * @return the itemsets or null if the user choose to save to file
	 * @throws IOException if exception while reading/writing to file
	 */
	public Itemsets runAlgorithm(double minimumSupport, Dataset dataset, String outputPath) throws IOException {
		// record the start time
		this.startTimestamp = System.currentTimeMillis();

		// if the user choose to save to file
		// create object for writing the output file
		if(outputPath != null) {
			this.writer = new BufferedWriter(new FileWriter(outputPath));
		}else {
			// if the user choose to save to memory
			this.writer = null;
			this.closedFrequentItemsets = new Itemsets("Itemsets");
		}

		// reset the number of itemset found
		this.frequentCount = 0;

		// reset the memory usage checking utility
		// VAS:commented_out MemoryLogger.getInstance().reset();

		// convert from an absolute minsup to a relative minsup by multiplying
		// by the database size
		this.minsupRelative = (int) Math.ceil(minimumSupport * dataset.getTransactions().size());

		// Create the initial occurrence array for the dataset
		performFirstOccurenceDelivery(dataset);

		//======
		// Remove infrequent items from transactions by using support calculated using
		// the buckets. Recall that each bucket correspond to an item
		// and contains the transactions where the items appears.
		for(Transaction transaction : dataset.getTransactions()) {
			transaction.removeInfrequentItems(this.buckets, this.minsupRelative);
		}

		//======
		// Create the array of all frequent items.
		List<Integer> allItems = new ArrayList<>();
		for(Integer item : dataset.getUniqueItems()) {
			if(this.buckets[item].size() >= this.minsupRelative) {
				allItems.add(item);
			}
		}
		// Sort all items
		Collections.sort(allItems);

		//======
		// Call the recursive method witht the empty set as prefix.
		// Since it is the empty set, we will have all transactions and no frequency count
		backtrackingLCM(null, dataset.getTransactions(), allItems, -1);

		// record the end time
		this.endTimestamp = System.currentTimeMillis();
		//close the output file
		if(this.writer != null) {
			this.writer.close();
		}

		// VAS:commented_out MemoryLogger.getInstance().checkMemory();

		return this.closedFrequentItemsets;
	}

	/**
	 * Recursive method to find closed itemsets
	 * @param p  a prefix itemset P
	 * @param transactionsOfP the transactions containing P
	 * @param frequentItems the list of frequent items in the p-projected database
	 * @param tailPosInP the tail item position in itemset P
	 * @throws IOException if error writing to output file
	 */
	private void backtrackingLCM(List<Integer> p,
			List<Transaction> transactionsOfP, List<Integer> frequentItems,
			int tailPosInP) throws IOException {

		// ========  for each frequent item  e  =============
		for (int j = 0; j < frequentItems.size(); j++) {
			Integer e = frequentItems.get(j);
			//   if the item is not already in p  before the current tail position
			// we will consider it to form a new closed itemset
			if((p != null) && containsByBinarySearch(p, e, tailPosInP)) {
				continue;
			}

			// Calculate transactions containing P U e
			// At the same time truncate the transactions to keep what appears after "e"
			List<Transaction> transactionsPe = intersectTransactions(transactionsOfP, e); //ok

			//====== Check if PU{e...} is a ppc extension  ======
			if (isPPCExtension(p, transactionsPe, e)) {
				//  ======= Create a closed itemset using PU{e...}  =====
				// First add all items from PU{e}
				List<Integer> itemset = new ArrayList<>();
				if(p != null) {
					//add every item i of p  such that i < e to the  itemset
					for (int m = 0; (m < p.size()) && (p.get(m) < e); m++) {
						itemset.add(p.get(m));
					}
				}
				itemset.add(e);
				int tailPositionInPe = itemset.size()-1;

				for (int k = j+1; k < frequentItems.size(); k++) {
					Integer itemk = frequentItems.get(k);
					// for every item i > e add if it is in all transactions of T(P U e)
					if(isItemInAllTransactions(transactionsPe, itemk)) {
						itemset.add(itemk);
					}
				}

				// ===== save the frequent closed itemset
				int supportPe = transactionsPe.size();
				output(itemset, supportPe);

				//==== perform database reduction ====
				anyTimeDatabaseReductionClosed(transactionsPe, j, frequentItems, p, e);

				// ================ Find frequent items in transactions containing P ============
				// Get all frequent items e such that e > tailOfP
				// (i.e. "e" appears after the position of the tail item in the list of all items)
				List<Integer> newFrequentItems = new ArrayList<>();
				for (int k = j+1; k < frequentItems.size(); k++) {
					Integer itemK =  frequentItems.get(k);
					int supportK = this.buckets[itemK].size();
					if(supportK >= this.minsupRelative) {
						newFrequentItems.add(itemK);
					}
				}

				// === recursive call
				backtrackingLCM(itemset, transactionsPe, newFrequentItems, tailPositionInPe);
			}
		}

		// VAS:commented_out MemoryLogger.getInstance().checkMemory();
	}


	/**
	 * Perform the initial occurence delivery with the original dataset
	 * containing all items
	 * @param dataset
	 */
	@SuppressWarnings("unchecked")
	public void performFirstOccurenceDelivery(Dataset dataset) {

		this.buckets = new List[dataset.getMaxItem() + 1];

		for (Integer item : dataset.uniqueItems) {
			this.buckets[item] = new ArrayList<>();
		}

		for (Transaction transaction : dataset.getTransactions()) {
			for (Integer item : transaction.getItems()) {
				// for each item get its bucket and add the current transaction
				this.buckets[item].add(transaction);
			}

		}
	}


	/**
	 * Perform the anytime database reduction for an itemset P U {e}
	 * @param transactions the transactions
	 * @param j the position of j in the list of frequent items
	 * @param frequentItems
	 * @param itemset
	 * @param e
	 */
	private void anyTimeDatabaseReductionClosed(List<Transaction> transactionsPe, int j, List<Integer> frequentItems, List<Integer> itemset, Integer e) {

		// We just reset the buckets for item  > e
		// instead of all buckets
		for (int i = j+1; i < frequentItems.size(); i++) {
			Integer item = frequentItems.get(i);
			this.buckets[item] = new ArrayList<>();
		}

		// for each transaction
		for(Transaction transaction : transactionsPe) {
			// we consider each item I  of the transaction such that  itemI > e
			for(int i = transaction.getItems().length-1; i >transaction.offset; i--) {
				Integer item = transaction.getItems()[i];
				if((item > e) && frequentItems.contains(item)) {
					// we add the transaction to the bucket of the itemI
					this.buckets[item].add(transaction);
				}
			}
		}
	}


	/**
	 * Check if an item appears in this itemset
	 * @param item  the item
	 * @return true if it appears. Otherwise, false.
	 */
	public boolean containsByBinarySearch(List<Integer> items, Integer item, int searchAfterPosition) {
		if((items.size() == 0) || (item > items.get(items.size() -1))) {
			return false;
		}
		int low = searchAfterPosition +1;
		int high = items.size() - 1;

		while (high >= low) {
			int middle = ( low + high ) >>> 1; // divide by 2
			if (items.get(middle).equals(item)) {
				return true;
			}
			if (items.get(middle) < item) {
				low = middle + 1;
			}
			if (items.get(middle) > item) {
				high = middle - 1;
			}
		}
		return false;
	}

	public boolean containsByBinarySearch(List<Integer> items, Integer item) {
		if((items.size() == 0) || (item > items.get(items.size() -1))) {
			return false;
		}
		int low = 0;
		int high = items.size() - 1;

		while (high >= low) {
			int middle = ( low + high ) >>> 1; // divide by 2
			if (items.get(middle).equals(item)) {
				return true;
			}
			if (items.get(middle) < item) {
				low = middle + 1;
			}
			if (items.get(middle) > item) {
				high = middle - 1;
			}
		}
		return false;
	}

	/**
	 * Calculate the transactions of the union of an itemset "P" with an item "e".
	 * @param transactionsOfP the transactions containing P
	 * @param e  the item "e"
	 * @return the transactions containing P U "e"
	 */
	public List<Transaction> intersectTransactions(List<Transaction> transactionsOfP, Integer e) {
		List<Transaction> transactionsPe = new ArrayList<>();

		// transactions of P U e
		for(Transaction transaction : transactionsOfP) {
			// we remember the position where e appears.
			// we will call this position an "offset"
			int posE = transaction.containsByBinarySearch(e);
			if (posE != -1) { // T(P U e)
				transactionsPe.add(new Transaction(transaction, posE));
			}
		}
		return transactionsPe;
	}


	/**
	 * Check if a given itemset PUe is a PPC extension according to
	 * the set of transactions containing PUe.
	 * @param p the itemset p
	 * @param e the item e
	 * @param transactionsPe  the transactions containing P U e
	 * @param previouslyAddedItem
	 * @return true if it is a PPC extension
	 */
	private boolean isPPCExtension(List<Integer> p, List<Transaction> transactionsPe, Integer e) {

		// We do a loop on each item i of the first transaction
		Transaction firstTrans = transactionsPe.get(0);
		Integer[] firstTransaction = firstTrans.getItems();
		for (int i = 0; i < firstTrans.offset; i++) {
			Integer item = firstTransaction[i];
			// if p does not contain item i < e and item i is present in all transactions,
			// then it PUe is not a ppc
			if((item < e) && ((p == null) || !containsByBinarySearch(p,item))
					&& isItemInAllTransactionsExceptFirst(transactionsPe, item)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an item appears in all transactions after the first one in a list of transactions
	 * @param transactions a list of transactions
	 * @param item an item
	 * @return true if the item appears in all transactions after the first one
	 */
	private boolean isItemInAllTransactionsExceptFirst(List<Transaction> transactions, Integer item) {

		for(int i=1; i < transactions.size(); i++) {
			if(transactions.get(i).containsByBinarySearchOriginalTransaction(item) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if an item appears in all transactions of a list of transactions
	 * @param transactions a list of transactions
	 * @param item an item
	 * @return true if the item appears in all transactions
	 */
	private boolean isItemInAllTransactions(List<Transaction> transactions, Integer item) {

		for(Transaction transaction : transactions) {
			if(transaction.containsByBinarySearch(item) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Save a frequent closed itemset to file or memory depending on what the user chose.
	 * @param itemset the itemset
	 * @throws IOException if error while writting to output file
	 */
	private void output(List<Integer> itemset, int support) throws IOException {
		// if not the empty set
		if(!itemset.isEmpty()) {
			this.frequentCount++;

			// if save to memory
			if(this.writer == null) {
				// The following line is not too optimized since
				// we convert an itemset as List<Integer> to int[]
				// but this cost is still quite small, so we leave it like
				this.closedFrequentItemsets.addItemset(new Itemset(itemset, support), itemset.size());
			}else {
				// if save to file
				// create a stringuffer
				StringBuilder buffer = new StringBuilder();
				// append items from the itemset to the StringBuilder
				for (int i = 0; i < itemset.size(); i++) {
					buffer.append(itemset.get(i));
					if (i != (itemset.size() - 1)) {
						buffer.append(' ');
					}
				}
				// append the support of the itemset
				buffer.append(" #SUP: ");
				buffer.append(support);
				// write the strinbuffer to file and create a new line
				// so that we are ready for writing the next itemset.
				this.writer.write(buffer.toString());
				this.writer.newLine();
			}
		}
	}




	/**
	 * Print statistics about the latest execution of the algorithm.
	 */
	public void printStats() {
		System.out.println("========== LCM - STATS ============");
		System.out.println(" Freq. closed itemsets count: " + this.frequentCount);
		System.out.println(" Total time ~: " + (this.endTimestamp - this.startTimestamp)
				+ " ms");
		// VAS:commented_out System.out.println(" Max memory:" +
		// MemoryLogger.getInstance().getMaxMemory());
		System.out.println("=====================================");
	}
}
