package com.dataminer.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.constant.Constant;
import com.dataminer.entity.LogFile;
import com.dataminer.pattern.itemset_array_integers_with_count.Itemsets;
import com.dataminer.util.HelperUtil;
import com.dataminer.util.MockUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
@Controller
public class IndexController extends BaseController {

	@GetMapping({ "/", "/index" })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
		processInputFile();
		boolean showStats = true;
		boolean showUploadOption = false;

		if (showStats) {
			modelAndView.addObject("commonItemSet", MockUtil.getMockUserEventList());
			modelAndView.addObject("commonItemSetTitle", Constant.commonItemSetTitle);

			modelAndView.addObject("rareItemSet", MockUtil.getMockRareUserEventList());
			modelAndView.addObject("rareItemSetTitle", Constant.rareItemSetTitle);

			modelAndView.addObject("surveyMap", MockUtil.getMockTimeSomething());
			modelAndView.addObject("surveyMapMaxValue", MockUtil.getMockTimeSomethingMaxValue());
		} else {
			modelAndView.addObject("commonItemSet", null);
			modelAndView.addObject("rareItemSet", null);
			modelAndView.addObject("surveyMap", null);
		}
		modelAndView.addObject("showStats", showStats);
		modelAndView.addObject("showUploadOption", showUploadOption);
		return view("index", modelAndView);
	}

	@PostMapping("/index")
	public ModelAndView uploadFile(@RequestParam("filename") MultipartFile file, RedirectAttributes redirectAttributes, ModelAndView mav) {
		// redirectAttributes.addFlashAttribute("upload_message", "You successfully uploaded " +
		// file.getOriginalFilename() + "!");
		// return redirect("index");
		String fileValue = "";
		String appleSauce = "";
		try {
			appleSauce = new String(file.getBytes());
		} catch (IOException e1) {
			log.debug(e1.getMessage());
		}

		try {
			fileValue = new String(appleSauce.getBytes("Cp1252"), "Cp1251");
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage());
		}

		System.out.println(appleSauce);
		System.out.println(fileValue);
		mav.addObject("upload_message", "You successfully uploaded " + file.getOriginalFilename() + "!");
		return view("index", mav);

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
			log.debug(e.getMessage());
		}
		runLCMFromLog(logFile);

		// System.out.println(logFile.toString());
		System.out.println("Successfully created " + logFile.getUserSessionList().size() + " user session!");
	}


	/////////// DEBUG ALGORITHMS

	public String runLCMFromLog(LogFile logFile) {
		int status = 0;
		Dataset dataset = new Dataset(logFile);
		double minsup = 0.2; // means a minsup of 2 transaction (we used a relative support)

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


}
