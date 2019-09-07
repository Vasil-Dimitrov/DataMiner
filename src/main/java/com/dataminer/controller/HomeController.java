package com.dataminer.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.constant.Constant;
import com.dataminer.constant.View;
import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.service.AlgoSettingsService;
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

	@Autowired
	private final AlgoSettingsService algoSettingsService;

	@Autowired
	public HomeController(AlgoSettingsService service) {
		this.algoSettingsService = service;
	}

	@GetMapping({ "/", View.INDEX_URL })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
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
	public ModelAndView uploadFile(@RequestParam("filename") MultipartFile mFile, ModelAndView mav) {
		AlgoSettings algoSettings = this.algoSettingsService.getDefaultAlgoSettings();
		boolean debug = true;

		if (algoSettings.areAllAlgorithmsDisabled()) {
			// add error
		} else {
			LogFile logFile = null;
			int sessionsCount = 0;
			if (!debug) {
				logFile = LogFile.createFromMultipartFile(mFile, algoSettings.getVtsa());
				sessionsCount = logFile.getUserSessionList().size();
			}

			if (algoSettings.getLcm()) {
				if (debug) {
					mav.addObject("commonItemSet", MockUtil.getMockUserEventList());
					mav.addObject("commonItemSetTitle", Constant.commonItemSetTitle);
				} else {
					Dataset dataset = new Dataset(logFile);
					Itemsets itemsets = new AlgoLCM().runAlgorithm(algoSettings.getLcmMinSup(), dataset);
					mav.addObject("commonItemSet", itemsets.getRelativeItemsets(sessionsCount));
					mav.addObject("commonItemSetTitle", Constant.commonItemSetTitle);
				}
			}
			if (algoSettings.getRpg()) {
				if (debug) {
					mav.addObject("rareItemSet", MockUtil.getMockRareUserEventList());
					mav.addObject("rareItemSetTitle", Constant.rareItemSetTitle);
				} else {
					Itemsets itemsets = new AlgoRPGrowth().runAlgorithm(logFile, algoSettings.getRpgMinSup(),
							algoSettings.getRpgMinRareSup());
					mav.addObject("rareItemSet", itemsets.getRelativeItemsets(sessionsCount));
					mav.addObject("rareItemSetTitle", Constant.rareItemSetTitle);
				}
			}
			if (algoSettings.getVtsa()) {
				if (debug) {
					mav.addObject("surveyMap", MockUtil.getMockTimeSomething());
					mav.addObject("surveyMapMaxValue", MockUtil.getMockTimeSomethingMaxValue());
				}
			}
		}

		// VASIL TODO: add proper logic here
		String fileText = "This will be the text in a file!";
		try {
			File file = new File(Constant.FILE_UPLOAD_DIR);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			fw.write(fileText);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mav.addObject("success_msg", "Файл " + mFile.getOriginalFilename() + " бе успешно обработен!");
		return view(View.INDEX_VIEW, mav);

	}

	@GetMapping("/download")
	@ResponseBody
	public FileSystemResource downloadFile() {
		File file = new File(Constant.FILE_UPLOAD_DIR);
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file, false);
				fw.write("Файлът не бе намерен, моля опитайте да генерирате фалът отново чрез извършване на нов анализ от лог");
				fw.close();
			} catch (IOException e) {
				log.debug("Something!");
				e.printStackTrace();
			}
		}
		return new FileSystemResource(new File(Constant.FILE_UPLOAD_DIR));
	}
}
