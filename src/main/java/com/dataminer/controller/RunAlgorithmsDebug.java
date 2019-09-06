package com.dataminer.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.constant.Constant;
import com.dataminer.util.HelperUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * This class exists soley for debug purposes and should eventually be discarded
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
@Controller
public class RunAlgorithmsDebug {

	@GetMapping("/runLCM")
	@ResponseBody
	public String runLCM(ModelAndView modelAndView) {
		int status = 0;
		String input;
		try {
			input = HelperUtil.fileToPath(Constant.debugFile);

			double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)
			Dataset dataset = new Dataset(input);

			// Applying the algorithm
			AlgoLCM algo = new AlgoLCM();
			// if true in next line it will find only closed itemsets, otherwise, all frequent
			// itemsets
			Itemsets itemsets = algo.runAlgorithm(minsup, dataset);
			algo.printStats();

			itemsets.printItemsets();
			status = 1;
		} catch (UnsupportedEncodingException e1) {
			log.debug(e1.getMessage());
		} catch (IOException e) {
			log.debug(e.getMessage());
		}

		return "LCM run completed with status " + status;
	}

	@GetMapping("/runRPGrowth")
	@ResponseBody
	public String runRPGrowth(ModelAndView modelAndView) {
		/*- This is only temporarily left for debug purposes
		int status = 0;
		String input;
		input = HelperUtil.fileToPath(Constant.debugFile);
		
		double minsup = 0.6; // means a minsup of 2 transaction (we used a relative support)
		double minraresup = 0.1;
		
		// Applying the algorithm
		AlgoRPGrowth algo = new AlgoRPGrowth();
		
		// Uncomment the following line to set the maximum pattern length (number of items per
		// itemset, e.g. 3 )
		algo.setMaximumPatternLength(3);
		
		// Run the algo
		// // NOTE that here we use "null" as the output file path because we are saving to memory
		Itemsets patterns = algo.runAlgorithm(input, null, minsup, minraresup);
		algo.printStats();
		//
		patterns.printItemsets();
		status = 1;
		 */

		return "This url is no logner available for call as the RPGGrowth run method has been changed too much to be used with a normal text file!";
	}
}
