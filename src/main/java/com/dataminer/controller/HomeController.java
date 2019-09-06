package com.dataminer.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.constant.Constant;
import com.dataminer.constant.View;
import com.dataminer.pojo.LogFile;
import com.dataminer.util.HelperUtil;
import com.dataminer.util.MockUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Home controller for working with the /index context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
@Controller
public class HomeController extends BaseController {

	@GetMapping({ "/", View.INDEX_URL })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
		processInputFile();
		boolean debug = false;
		boolean showUploadOption = true;

		if (debug) {
			modelAndView.addObject("commonItemSet", MockUtil.getMockUserEventList());
			modelAndView.addObject("commonItemSetTitle", Constant.commonItemSetTitle);

			modelAndView.addObject("rareItemSet", MockUtil.getMockRareUserEventList());
			modelAndView.addObject("rareItemSetTitle", Constant.rareItemSetTitle);

			modelAndView.addObject("surveyMap", MockUtil.getMockTimeSomething());
			modelAndView.addObject("surveyMapMaxValue", MockUtil.getMockTimeSomethingMaxValue());
		}

		modelAndView.addObject("showUploadOption", showUploadOption);
		return view(View.INDEX_VIEW, modelAndView);
	}

	@PostMapping(View.INDEX_URL)
	public ModelAndView uploadFile(@RequestParam("filename") MultipartFile file, ModelAndView mav) {

		List<String> result2 = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1))) {
			String line;
			while ((line = br.readLine()) != null) {
				result2.add(new String(line.getBytes("Cp1252"), "Cp1251"));
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		for (String txt : result2) {
			System.out.println(txt);
			System.out.println("\n=============================================================================================\n");
		}

		mav.addObject("upload_message", "You successfully uploaded " + file.getOriginalFilename() + "!");
		return view(View.INDEX_VIEW, mav);

	}

	private void processInputFile() {
		String filePath = HelperUtil.fileToPath(Constant.debugReadFile);
		LogFile logFile = new LogFile();

		try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1)) {
			String line;
			String tempText;
			for (int i = 0; (line = br.readLine()) != null; i++) {
				if (i == 0) {
					continue;
				}

				tempText = new String(line.getBytes("Cp1252"), "Cp1251");
				logFile.addLine(tempText);
			}
		} catch (IOException e) {
			log.error(e.toString());
		}
		// runLCMFromLog(logFile);
		// runRPGGrowthFromLog(logFile);

		System.out.println(logFile.toString());
		System.out.println("Successfully created " + logFile.getUserSessionList().size() + " user session!");
	}

	/////////// DEBUG ALGORITHMS

	public String runLCMFromLog(LogFile logFile) {
		int status = 0;
		double minsup = 0.2;
		Dataset dataset = new Dataset(logFile);

		// Applying the algorithm
		AlgoLCM algo = new AlgoLCM();
		// if true in next line it will find only closed itemsets, otherwise, all frequent
		// itemsets
		Itemsets itemsets = algo.runAlgorithm(minsup, dataset);
		itemsets.sortItemsets();
		algo.printStats();

		itemsets.printItemsets();
		status = 1;

		return "LCM run completed with status " + status;
	}

	public String runRPGGrowthFromLog(LogFile logFile) {
		int status = 0;
		double minsup = 0.32;
		double minraresup = 0.15;

		AlgoRPGrowth algo = new AlgoRPGrowth();

		Itemsets patterns = null;

		try {
			patterns = algo.runAlgorithm(logFile, minsup, minraresup);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		algo.printStats();

		patterns.printItemsets();
		status = 1;
		return "RPGrowth run completed with status " + status;
	}

}
