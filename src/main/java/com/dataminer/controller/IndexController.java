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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dataminer.algorithm.AlgoAprioriClose;
import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.pattern.itemset_array_integers_with_count.Itemsets;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class IndexController extends BaseController {
	private int status = 0;
	private String debugFile = "contextPasquier99.txt";
	private String debugReadFile = "logs_BCS37_20181103_UTF-8.csv";

	@GetMapping({ "/", "/index" })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
		processInputFile();
		return view("index");
	}

	@PostMapping("/index")
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, ModelAndView mav) {
		// redirectAttributes.addFlashAttribute("upload_message", "You successfully uploaded " +
		// file.getOriginalFilename() + "!");
		// return redirect("index");
		String fileValue = "";
		String appleSauce = "";
		try {
			appleSauce = new String(file.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			fileValue = new String(appleSauce.getBytes("Cp1252"), "Cp1251");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(appleSauce);
		System.out.println(fileValue);
		mav.addObject("upload_message", "You successfully uploaded " + file.getOriginalFilename() + "!");
		return view("index", mav);

	}

	private void processInputFile() {
		String filePath = fileToPath(this.debugReadFile);
		// read file into stream, try-with-resources

		try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1)) {
			String line;
			String tempText;
			int counter = 0;
			while ((line = br.readLine()) != null) {
				tempText = new String(line.getBytes("Cp1252"), "Cp1251");
				System.out.println(tempText);
				counter++;
				if (counter >= 20) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// left over for debug purposes
	@GetMapping("/login")
	public String showLoginForm() {

		return "views/loginForm";
	}

	@RequestMapping(value = "/runApriori", method = RequestMethod.GET)
	@ResponseBody
	public String runAprioriClose(ModelAndView modelAndView) {
		this.status = 0;
		String input;
		try {
			input = fileToPath(this.debugFile);

			String output = null;
			// Note : we here set the output file path to null
			// because we want that the algorithm save the
			// result in memory for this example.

			double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)

			// Applying the Apriori algorithm
			AlgoAprioriClose apriori = new AlgoAprioriClose();
			Itemsets result;

			result = apriori.runAlgorithm(minsup, input, output);
			apriori.printStats();
			result.printItemsets();
			this.status = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Apriori run completed with status " + this.status;
	}

	@GetMapping("/runLCM")
	@ResponseBody
	public String runLCM(ModelAndView modelAndView) {
		this.status = 0;
		String input;
		try {
			input = fileToPath(this.debugFile);

			double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)
			Dataset dataset = new Dataset(input);

			// Applying the algorithm
			AlgoLCM algo = new AlgoLCM();
			// if true in next line it will find only closed itemsets, otherwise, all frequent
			// itemsets
			Itemsets itemsets = algo.runAlgorithm(minsup, dataset, null);
			algo.printStats();

			itemsets.printItemsets();
			this.status = 1;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "LCM run completed with status " + this.status;
	}

	@GetMapping("/runRPGrowth")
	@ResponseBody
	public String runRPGrowth(ModelAndView modelAndView) {
		this.status = 0;
		String input;
		try {
			input = fileToPath(this.debugFile);

			double minsup = 0.6; // means a minsup of 2 transaction (we used a relative support)
			double minraresup = 0.1;

			// Applying the algorithm
			AlgoRPGrowth algo = new AlgoRPGrowth();

			// Uncomment the following line to set the maximum pattern length (number of items per
			// itemset, e.g. 3 )
			// algo.setMaximumPatternLength(3);

			// Run the algo
			// NOTE that here we use "null" as the output file path because we are saving to memory
			Itemsets patterns = algo.runAlgorithm(input, null, minsup, minraresup);
			algo.printStats();

			patterns.printItemsets();
			this.status = 1;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "RPGrowth run completed with status " + this.status;
	}

	public static String fileToPath(String filename) {
		String userDir = System.getProperty("user.dir") + "\\test_files\\" + filename;
		// URL url = IndexController.class.getResource(filename);
		try {
			return java.net.URLDecoder.decode(userDir, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "ako";
		}
	}
}