package com.dataminer.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
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
import com.dataminer.constant.RequestAttribute;
import com.dataminer.constant.View;
import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.service.AlgoSettingsService;
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

	private final AlgoSettingsService algoSettingsService;

	@Autowired
	public HomeController(AlgoSettingsService service) {
		this.algoSettingsService = service;
	}

	@GetMapping({ "/", View.INDEX_URL })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
		return view(View.INDEX_VIEW, modelAndView);
	}

	/**
	 * Method handling the log file upload to server as well as the analysis made by the
	 * different algorithms
	 *
	 * @param mFile
	 * @param mav
	 * @return
	 */
	@PostMapping(View.INDEX_URL)
	public ModelAndView uploadFile(@RequestParam(RequestAttribute.FILE) MultipartFile mFile, ModelAndView mav) {
		AlgoSettings algoSettings = this.algoSettingsService.getDefaultAlgoSettings();
		boolean debug = false;
		if (debug) {
			return view(View.INDEX_VIEW, mockDataOnly(mav, algoSettings, mFile));
		}

		if (algoSettings.areAllAlgorithmsDisabled()) {
			mav.addObject(RequestAttribute.ERROR_MSG, "Моля активирайте поне един алгоритъм от настройките!");
			return view(View.INDEX_VIEW, mav);
		}

		LogFile logFile = LogFile.createFromMultipartFile(mFile, algoSettings.getVtsa());

		StringBuilder statsDataForFileSave = new StringBuilder();
		if (algoSettings.getLcm()) {
			Dataset dataset = new Dataset(logFile);
			AlgoLCM algo = new AlgoLCM();
			Itemsets itemsets = algo.runAlgorithm(algoSettings.getLcmMinSup(), dataset);
			Map<String, Double> resultItemset = itemsets.getRelativeItemsets(logFile);
			mav.addObject(RequestAttribute.LCM_DATA, resultItemset);
			mav.addObject(RequestAttribute.LCM_TITLE, Constant.LCM_TITLE_TEXT);

			if (!CollectionUtils.isEmpty(resultItemset)) {
				statsDataForFileSave.append(itemsets.getAnalysisForFile(logFile));
				statsDataForFileSave.append(algo.getStatsForFile());
			}
		}
		if (algoSettings.getRpg()) {
			AlgoRPGrowth algo = new AlgoRPGrowth();
			Itemsets itemsets = algo.runAlgorithm(logFile, algoSettings.getRpgMinSup(), algoSettings.getRpgMinRareSup());
			Map<String, Double> resultItemset = itemsets.getRelativeItemsets(logFile);
			mav.addObject(RequestAttribute.RPG_DATA, resultItemset);
			mav.addObject(RequestAttribute.RPG_TITLE, Constant.RPG_TITLE_TEXT);

			if (!CollectionUtils.isEmpty(resultItemset)) {
				statsDataForFileSave.append(itemsets.getAnalysisForFile(logFile));
				statsDataForFileSave.append(algo.getStatsForFile());
			}
		}
		if (algoSettings.getVtsa()) {
			mav.addObject(RequestAttribute.VTSA_DATA, MockUtil.getMockTimeSomething());
			mav.addObject(RequestAttribute.VTSA_TITLE, MockUtil.getMockTimeSomethingMaxValue());
		}
		
		
		if(HelperUtil.generateFile(statsDataForFileSave.toString(), logFile.getUserSessionList().size())) {
			mav.addObject(RequestAttribute.SUCCESS_MSG, "Файл " + mFile.getOriginalFilename() + " бе успешно обработен!");
		}

		return view(View.INDEX_VIEW, mav);
	}
	

	public ModelAndView mockDataOnly(ModelAndView mav, AlgoSettings algoSettings, MultipartFile mFile) {
		if (algoSettings.getLcm()) {
			mav.addObject(RequestAttribute.LCM_DATA, MockUtil.getMockUserEventList());
			mav.addObject(RequestAttribute.LCM_TITLE, Constant.LCM_TITLE_TEXT);
		}
		if (algoSettings.getRpg()) {
			mav.addObject(RequestAttribute.RPG_DATA, MockUtil.getMockRareUserEventList());
			mav.addObject(RequestAttribute.RPG_TITLE, Constant.RPG_TITLE_TEXT);
		}
		if (algoSettings.getVtsa()) {
			mav.addObject(RequestAttribute.VTSA_DATA, MockUtil.getMockTimeSomething());
			mav.addObject(RequestAttribute.VTSA_TITLE, MockUtil.getMockTimeSomethingMaxValue());
		}
		mav.addObject(RequestAttribute.SUCCESS_MSG, "Файл " + mFile.getOriginalFilename() + " бе успешно обработен!");

		return mav;
	}

	/**
	 * Method returning analysis_file.txt file with more detailed algorithm analysis made on
	 * the uploaded log file
	 *
	 * @return
	 */
	@GetMapping(View.DOWNLOAD_URL)
	@ResponseBody
	public FileSystemResource downloadFile() {
		File file = new File(Constant.FILE_UPLOAD_DIR);
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file, false);
				fw.write("Файлът не бе намерен, моля опитайте да генерирате фалът отново чрез извършване на нов анализ от лог");
				fw.close();
				log.warn("Client tried retrieving file with analysis, but it was missing!");
			} catch (IOException e) {
				log.error("Exception occurred trying to recreate a misssing analysis file!", e);
			}
		}
		return new FileSystemResource(new File(Constant.FILE_UPLOAD_DIR));
	}
}
