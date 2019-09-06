package com.dataminer.algorithm.lcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.UserSession;

import lombok.Getter;

/**
 * This is the parser class for the dataset. It has actions related to parse a
 * {@link LogFile} class to a Dataset class.
 *
 * @see AlgoLCM
 */
@Getter
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


	/**
	 * Method for creating a {@link Transaction} out of {@link UserSession}
	 *
	 * @param userSession
	 * @return
	 */
	private Transaction createTransactionFromUserSession(UserSession userSession) {
		Integer[] itemsSorted = userSession.getEventContextKeySet().toArray(new Integer[0]);
		this.uniqueItems.addAll(userSession.getEventContextKeySet());

		int lastItem = userSession.getEventContextKeySet().last();
		if (lastItem > this.maxItem) {
			this.maxItem = lastItem;
		}

		return new Transaction(itemsSorted);
	}

}
