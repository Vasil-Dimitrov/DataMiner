package com.dataminer.algorithm.rpgrowth;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.dataminer.algorithm.pattern.Itemset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.UserSession;

/**
 * This is an optimized implementation of the Rare Pattern Tree Mining algorithm using the
 * FP-Growth algorithm.
 *
 * @author Vasil.Dimitrov^2
 *
 */
public class AlgoRPGrowth {

	//for statistics
	private long startTimestamp; // start time of the latest execution
	private long endTime; // end time of the latest execution
	private int transactionCount = 0; // transaction count in the database
	private int itemsetCount; // number of freq. itemsets found

	// parameter
	public int minRareSupportRelative;//the relative minimum rare support
	public int minSupportRelative; // the relative minimum support

	// The  patterns that are found
	// (if the user want to keep them into memory)
	protected Itemsets patterns = null;

	// This variable is used to determine the size of buffers to store itemsets.
	// A value of 50 is enough because it allows up to 2^50 patterns!
	final int BUFFERS_SIZE = 2000;

	// buffer for storing the current itemset that is mined when performing mining
	// the idea is to always reuse the same buffer to reduce memory usage.
	private int[] itemsetBuffer = null;
	// another buffer for storing rpnodes in a single path of the tree
	private RPNode[] rpNodeTempBuffer = null;


	/** maximum pattern length */
	private int maxPatternLength = 1000;

	/**
	 * Run the algorithm
	 *
	 * @param logFile
	 * @param minsupp
	 * @param minraresupp
	 * @return
	 */
	public Itemsets runAlgorithm(LogFile logFile, double minsupp, double minraresupp) {
		this.startTimestamp = System.currentTimeMillis();
		this.itemsetCount = 0;

		// if the user want to keep the result into memory
		this.patterns = new Itemsets("RARE ITEMSETS");

		// (1) PREPROCESSING: frequency of each item
		// The frequency is stored in a map:
		// final Map<Integer, Integer> mapSupport =
		// scanDatabaseToDetermineFrequencyOfSingleItems(input);

		// convert the minimum support as percentage to a relative minimum support
		// convert the minimum rare support as percentage to a minimum rare support
		this.transactionCount = logFile.getUserSessionList().size();
		this.minRareSupportRelative = (int) Math.ceil(minraresupp * this.transactionCount);
		this.minSupportRelative = (int) Math.ceil(minsupp * this.transactionCount);

		// (2) Scan the database again to build the initial RP-Tree
		// Before inserting a transaction in the RPTree, we sort the items
		// by descending order of support. We ignore items that
		// have over the minimum support.
		RPTree tree = new RPTree();

		for (UserSession userSession : logFile.getUserSessionList()) {
			List<Integer> transaction = new ArrayList<>();

			for (Integer contextKey : userSession.getEventContextKeySet()) {
				// only add items that have less than or equal to the minimum support
				// and more than or equal to the minimum rare support
				if (logFile.getKeyCount().get(contextKey) >= this.minRareSupportRelative) {
					// so the items being added are >= minRareSupportRelative
					transaction.add(contextKey);
				}
			}

			if (CollectionUtils.isEmpty(transaction)) {
				continue;
			}

			// sort item in the transaction by descending order of support
			Collections.sort(transaction, new Comparator<Integer>() {
				@Override
				public int compare(Integer item1, Integer item2) {
					// compare the frequency
					int compare = logFile.getKeyCount().get(item2) - logFile.getKeyCount().get(item1);
					// if the same frequency, we check the lexical ordering!
					if (compare == 0) {
						return (item1 - item2);
					}
					// otherwise, just use the frequency
					return compare;
				}
			});

			// Add the sorted items to the RP tree
			// If (last item in sorted transaction is < minRelSup, we accept the transaction)
			// get the last item in transaction; because the last item in the transaction is the
			// smallest count size
			int biggestTransactionKey = transaction.get(transaction.size() - 1);
			// take item and get its count
			int count = logFile.getKeyCount().get(biggestTransactionKey);
			// if the last item is below minSupportRelative then it is Rare by our definition, so it
			// is of interest and added to the tree
			if (count < this.minSupportRelative) {
				tree.addTransaction(transaction);
			}
		}

		// We create the header table for the tree using the calculated support of single items
		tree.createHeaderList(logFile.getKeyCount());

		// (5) We start to mine the RP-Tree by calling the recursive method.
		// Initially, the prefix alpha is empty.
		// if at least one item is not frequent
		if(tree.headerList.size() > 0) {
			// initialize the buffer for storing the current itemset
			this.itemsetBuffer = new int[this.BUFFERS_SIZE];
			// and another buffer
			this.rpNodeTempBuffer = new RPNode[this.BUFFERS_SIZE];
			// recursively generate rare itemsets using the RP-tree
			// Note: we assume that the initial RP-Tree has more than one path
			// which should generally be the case.
			rpgrowth(tree, this.itemsetBuffer, 0, this.transactionCount, logFile.getKeyCount());
		}

		this.endTime= System.currentTimeMillis();
		return this.patterns;
	}



	/**
	 * Mine an RP-Tree having more than one path.
	 *
	 * @param tree
	 * @param prefix
	 * @param prefixLength
	 * @param prefixSupport
	 * @param mapSupport
	 */
	private void rpgrowth(RPTree tree, int[] prefix, int prefixLength, int prefixSupport, Map<Integer, Integer> mapSupport) {

		if(prefixLength == this.maxPatternLength) {
			return;
		}


		////		======= DEBUG ========
		//				System.out.print("###### Prefix: ");
		//				for(int k=0; k< prefixLength; k++) {
		//					System.out.print(prefix[k] + "  ");
		//				}
		//				System.out.println("\n");
		////						========== END DEBUG =======
		//				System.out.println(tree); --constructs a visual representation of the tree to assist with debugging.

		// We will check if the RPtree contains a single path
		boolean singlePath = true;
		// This variable is used to count the number of items in the single path
		// if there is one
		int position = 0;
		// if the root has more than one child, than it is not a single path
		if(tree.root.childs.size() > 1) {
			singlePath = false;
		}else {
			// Otherwise,
			// if the root has exactly one child, we need to recursively check children
			// of the child to see if they also have one child
			RPNode currentNode = tree.root.childs.get(0);
			while(true){
				// if the current child has more than one child, it is not a single path!
				if(currentNode.childs.size() > 1) {
					singlePath = false;
					break;
				}
				// otherwise, we copy the current item in the buffer and move to the child
				// the buffer will be used to store all items in the path
				this.rpNodeTempBuffer[position] = currentNode;

				position++;
				// if this node has no child, that means that this is the end of this path
				// and it is a single path, so we break
				if(currentNode.childs.size() == 0) {
					break;
				}
				currentNode = currentNode.childs.get(0);
			}
		}
		// Case 1: the RPtree contains a single path
		//If the prefix is NOT the root and there is a single path
		if ((singlePath) && (prefixLength > 0))
		{
			saveAllCombinationsOfPrefixPath(this.rpNodeTempBuffer, position, prefix, prefixLength);
		}
		else {
			// For each rare item in the header table list of the tree in reverse order.
			for(int i = tree.headerList.size()-1; i>=0; i--){
				// get the item
				Integer item = tree.headerList.get(i);

				// get the item support
				int support = mapSupport.get(item);
				if((prefixLength == 0) && (support >= this.minSupportRelative)) {
					return;
				}
				// Create Beta by concatenating prefix Alpha by adding the current item to alpha
				prefix[prefixLength] = item;

				// calculate the support of the new prefix beta
				int betaSupport = (prefixSupport < support) ? prefixSupport: support;

				// save beta to the output file

				//If not the root OR support < minimum relative support; save item set
				if ((prefixLength > 0) || (support < this.minSupportRelative)) {
					setSupport(prefix, prefixLength+1, betaSupport);
				}


				if((prefixLength+1) < this.maxPatternLength){
					// === (A) Construct beta's conditional pattern base ===
					// It is a sub-database which consists of the set of prefix paths
					// in the RP-tree co-occurring with the prefix pattern.
					List<List<RPNode>> prefixPaths = new ArrayList<>();
					RPNode path = tree.mapItemNodes.get(item);

					// Map to count the support of items in the conditional prefix tree
					// Key: item   Value: support
					Map<Integer, Integer> mapSupportBeta = new HashMap<>();

					while(path != null){
						// if the path is not just the root node
						if(path.parent.itemID != -1){
							// create the prefixpath
							List<RPNode> prefixPath = new ArrayList<>();
							// add this node.
							prefixPath.add(path);   // NOTE: we add it just to keep its support,
							// actually it should not be part of the prefixPath
							int pathCount = path.counter;

							//Recursively add all the parents of this node.
							RPNode parent = path.parent;
							while(parent.itemID != -1){
								prefixPath.add(parent);

								// FOR EACH PATTERN WE ALSO UPDATE THE ITEM SUPPORT AT THE SAME TIME
								// if the first time we see that node id
								if(mapSupportBeta.get(parent.itemID) == null){
									// just add the path count
									mapSupportBeta.put(parent.itemID, pathCount);
								}else{
									// otherwise, make the sum with the value already stored
									mapSupportBeta.put(parent.itemID, mapSupportBeta.get(parent.itemID) + pathCount);
								}
								parent = parent.parent;
							}
							// add the path to the list of prefix paths
							prefixPaths.add(prefixPath);
						}
						// We will look for the next prefix path
						path = path.nodeLink;
					}

					// (B) Construct beta's conditional RP-Tree
					// Create the tree.
					RPTree treeBeta = new RPTree();
					// Add each prefix path in the RP-tree.
					for(List<RPNode> prefixPath : prefixPaths){
						treeBeta.addPrefixPath(prefixPath, mapSupportBeta, this.minSupportRelative, this.minRareSupportRelative);
					}

					// Mine recursively the Beta tree if the root has child(s)
					if(treeBeta.root.childs.size() > 0){
						// Create the header list.
						treeBeta.createHeaderList(mapSupportBeta);

						// recursive call
						rpgrowth(treeBeta, prefix, prefixLength+1, betaSupport, mapSupportBeta);
					}
				}
			}
		}
	}


	/**
	 * This method saves all combinations of a prefix path if it has enough support
	 *
	 * @param rpNodeTempBuffer
	 * @param position
	 * @param prefix
	 * @param prefixLength
	 */
	private void saveAllCombinationsOfPrefixPath(RPNode[] rpNodeTempBuffer, int position,
			int[] prefix, int prefixLength) {
		int support = 0;
		if (prefixLength == 0) {
			return;}

		// Generate all subsets of the prefixPath except the empty set
		// and output them
		// We use bits to generate all subsets.
		loop1:	for (long i = 1, max = 1 << position; i < max; i++) {
			// we create a new subset
			int newPrefixLength = prefixLength;
			// for each bit
			for (int j = 0; j < position; j++) {
				// check if the j bit is set to 1
				int isSet = (int) i & (1 << j);
				// if yes, add the bit position as an item to the new subset
				if (isSet > 0) {
					if(newPrefixLength == this.maxPatternLength){
						continue loop1;
					}

					prefix[newPrefixLength++] = rpNodeTempBuffer[j].itemID;
					support = rpNodeTempBuffer[j].counter;
				}
			}
			// save the item set
			setSupport(prefix, newPrefixLength, support);
		}
	}



	/**
	 * Write a frequent item set that is found to the output file or keep into memory if the
	 * user prefer that the result be saved into memory.
	 *
	 * @param itemset
	 * @param itemsetLength
	 * @param support
	 */
	private void setSupport(int[] itemset, int itemsetLength, int support) {

		// increase the number of item sets found for statistics purpose
		this.itemsetCount++;

		// create an object Itemset and add it to the set of patterns
		// found.
		int[] itemsetArray = new int[itemsetLength];
		System.arraycopy(itemset, 0, itemsetArray, 0, itemsetLength);

		// sort the itemset so that it is sorted according to lexical ordering before we show it
		// to the user
		Arrays.sort(itemsetArray);

		Itemset itemsetObj = new Itemset(itemsetArray);
		itemsetObj.setSupport(support);
		this.patterns.addItemset(itemsetObj, itemsetLength);
	}

	/**
	 * Print statistics about the algorithm execution to System.out.
	 */
	public void printStats() {
		System.out.println("=============  RP-GROWTH 2.38 - STATS =============");
		long temps = this.endTime - this.startTimestamp;
		System.out.println(" Transactions count from database : " + this.transactionCount);
		System.out.println(" Rare itemsets count : " + this.itemsetCount);
		System.out.println(" Total time ~ " + temps + " ms");
		System.out.println("===================================================");
	}

	/**
	 * Get the number of transactions in the last transaction database read.
	 * @return the number of transactions.
	 */
	public int getDatabaseSize() {
		return this.transactionCount;
	}

	/**
	 * Set the maximum pattern length
	 * @param length the maximum length
	 */
	public void setMaximumPatternLength(int length) {
		this.maxPatternLength = length;
	}
}