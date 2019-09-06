package com.dataminer.algorithm.rpgrowth;



import java.util.ArrayList;
import java.util.List;

import com.dataminer.algorithm.pattern.Itemset;

/**
 * This is an implementation of a RPTree node as used by the RPGrowth algorithm.
 *
 * @see FPTree
 * @see RPTree
 * @see Itemset
 * @see AlgoRPGrowth
 */
public class RPNode {
	int itemID = -1;  // item id
	int counter = 1;  // frequency counter  (a.k.a. support)

	// the parent node of that node or null if it is the root
	RPNode parent = null;
	// the child nodes of that node
	List<RPNode> childs = new ArrayList<>();

	RPNode nodeLink = null; // link to next node with the same item id (for the header table).

	/**
	 * constructor
	 */
	RPNode(){

	}

	/**
	 * Return the immediate child of this node having a given ID.
	 * If there is no such child, return null;
	 */
	RPNode getChildWithID(int id) {
		// for each child node
		for(RPNode child : this.childs){
			// if the id is the one that we are looking for
			if(child.itemID == id){
				// return that node
				return child;
			}
		}
		// if not found, return null
		return null;
	}

	/**
	 * Method for getting a string representation of this tree
	 * (to be used for debugging purposes).
	 * @param an indentation
	 * @return a string
	 */
	public String toString(String indent) {
		StringBuilder output = new StringBuilder();
		output.append(""+ this.itemID);
		output.append(" (count="+ this.counter);
		output.append(")\n");
		String newIndent = indent + "   ";
		for (RPNode child : this.childs) {
			output.append(newIndent+ child.toString(newIndent));
		}
		return output.toString();
	}

	@Override
	public String toString() {
		return ""+this.itemID;
	}
}
