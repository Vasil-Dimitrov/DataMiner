package com.dataminer.algorithm.lcm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.UserSession;
/**
 * This is the parser class for the dataset. It has actions related to parse a txt based
 * file to a Dataset class.
 *
 * @see AlgoLCM
 */
public class Dataset {

	private List<Transaction> transactions;
	// this is basically the uniqueItems element but sorted (for optimization)
	private Integer[] transactionsItems;

	Set<Integer> uniqueItems = new HashSet<>();

	private int maxItem = 0;

	public Dataset(LogFile logFile) {
		this.transactions = new ArrayList<>();

		for (UserSession userSession : logFile.getUserSessionList()) {
			this.transactions.add(createTransactionFromUserSession(userSession));
		}

		/// sort transactions by increasing last item (optimization)
		Collections.sort(this.transactions, new Comparator<Transaction>() {
			@Override
			public int compare(Transaction arg0, Transaction arg1) {
				// return arg0.getItems().length - arg1.getItems().length;
				return arg0.getItems()[arg0.getItems().length - 1] - arg1.getItems()[arg1.getItems().length - 1];
			}
		});

		// create the list of items in the database and sort it
		this.transactionsItems = new Integer[this.uniqueItems.size()];
		int i = 0;
		for (Integer item : this.uniqueItems) {
			this.transactionsItems[i++] = item;
		}
		Arrays.sort(this.transactionsItems);
	}

	public Dataset(String datasetPath) throws IOException {

		this.transactions = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(datasetPath));
		String items;
		while((items = br.readLine()) != null) { // iterate over the lines to build the transaction
			// if the line is  a comment, is  empty or is  metadata
			if ((items.isEmpty() == true) || (items.charAt(0) == '#')
					|| (items.charAt(0) == '%') || (items.charAt(0) == '@')) {
				continue;
			}

			getTransactions().add(createTransaction(items));
		}
		br.close();

		/// sort transactions by increasing last item (optimization)
		Collections.sort(this.transactions, new Comparator<Transaction>() {
			@Override
			public int compare(Transaction arg0, Transaction arg1) {
				//				return arg0.getItems().length - arg1.getItems().length;
				return arg0.getItems()[arg0.getItems().length -1] -
						arg1.getItems()[arg1.getItems().length -1];
			}});

		// create the list of items in the database and sort it
		this.transactionsItems = new Integer[this.uniqueItems.size()];
		int i=0;
		for(Integer item : this.uniqueItems) {
			this.transactionsItems[i++] = item;
		}
		Arrays.sort(this.transactionsItems);

	}

	/**
	 * Create a transaction object from a line from the input file
	 * @param line a line from input file
	 * @return a transaction
	 */
	private Transaction createTransaction(String line) {

		//build the items
		Pattern splitPattern = Pattern.compile(" ");
		String[] items = splitPattern.split(line);

		Integer[] itemsSorted = new  Integer[items.length];

		for (int i = 0; i < items.length; i++) {
			Integer item = Integer.valueOf(items[i]);
			itemsSorted[i] = item;

			this.uniqueItems.add(item);
		}

		// update max item by checking the last item of the transaction
		int lastItem = itemsSorted[itemsSorted.length - 1];
		if(lastItem > this.maxItem) {
			this.maxItem = lastItem;
		}
		return new Transaction(itemsSorted);
	}

	/**
	 * Method for creating a {@link Transaction} out of {@link UserSession}
	 *
	 * @param userSession
	 * @return
	 */
	public Transaction createTransactionFromUserSession(UserSession userSession) {
		Integer[] itemsSorted = userSession.getEventContextKeySet().toArray(new Integer[0]);
		this.uniqueItems.addAll(userSession.getEventContextKeySet());

		int lastItem = userSession.getEventContextKeySet().last();
		if (lastItem > this.maxItem) {
			this.maxItem = lastItem;
		}

		return new Transaction(itemsSorted);
	}

	public List<Transaction> getTransactions() {
		return this.transactions;
	}

	public Set<Integer> getUniqueItems() {
		return this.uniqueItems;
	}


	//    public Integer[] getAllItems() {
	//
	//        return transactionsItems;
	//    }

	public int getMaxItem() {
		return this.maxItem;
	}

	@Override
	public String toString() {
		StringBuilder datasetContent = new StringBuilder();

		for(Transaction transaction : this.transactions) {
			datasetContent.append(transaction);
			datasetContent.append("\n");
		}
		return datasetContent.toString();
	}

}
