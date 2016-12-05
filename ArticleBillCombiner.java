package ArticleFetcher;

import com.google.gson.JsonSyntaxException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ArticleFetcher.Bill.Title;

public class ArticleBillCombiner {
	
	//First congress we have full data for
	private static final int FIRST_GOVTRACK_CONGRESS = 93;
	private static final int FIRST_FULLTEXT_CONGRESS = 97;
	private static final int CURRENT_CONGRESS = 114;
	private static final int JANUARY = 1;
	private static final int DECEMBER = 12;
	private static final int TEMP_START_CONGRESS = 103;
	
	private static boolean isWindows = false;
	private static boolean isFull = false;
	private static boolean isFirst = false;
	private static boolean isLast = false;
	private static boolean isShort = false;
	private static String windowsBillDirectory = "C:\\cygwin64\\home\\sem129\\GovTrackData\\govTrackJsons\\";
	private static String windowsArticleDirectory = "C:\\cygwin64\\home\\sem129\\GovTrackData\\ArticleBillDatabase\\ArticleBillDatabase\\NYT_raw\\";
	private static String articleFilename = "NYTarchive_";
	private static String windowsOutputDirectory = "C:\\cygwin64\\home\\sem129\\GovTrackData\\output\\";
	private static String linuxOutputDirectory = "../output/";
	private static String linuxBillDirectory = "../govTrackJsons/";
	private static String linuxArticleDirectory = "../NYT_raw/";
	
	public static void main(String[] args) {
		if(args.length == 0){
			throw new IllegalArgumentException("Please specify short (first and last congress) or full (all congresses).");
		}
		else if(args[0].toLowerCase().equals("short")){
			isShort = true;
		}
		else if(args[0].toLowerCase().equals("full")){
			isFull = true;
		}
		else if(args[0].equals("first")){
			isFirst = true;
		}
		else if(args[0].equals("last")){
			isLast = true;
		}
		else throw new IllegalArgumentException("Please specify short (first and last congress) or full (all congresses).");
		
		if(args.length > 1){
			String system = args[1];
			if(system != null && system.toLowerCase() == "windows"){
				isWindows = true;
			}
		}

		try{

			System.out.println("Hello I am start.");
			if(isFull){
				for(int congress = FIRST_FULLTEXT_CONGRESS; congress < CURRENT_CONGRESS; congress++){
					execute(congress);
				}
			}
			else if(isShort){
				execute(FIRST_FULLTEXT_CONGRESS);
				execute(CURRENT_CONGRESS - 1);
			}
			else if(isFirst){
				execute(FIRST_FULLTEXT_CONGRESS);
			}
			else if(isLast){
				execute(CURRENT_CONGRESS - 1);
			}
			
			
			System.out.println("Done: " + System.currentTimeMillis());
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	private static void execute(int congress) throws IOException {
		/*
		 * Iterable hash map.
		 * This data structure chosen for the ability to quickly find bills by key (for merging)
		 * and also its iterability. It is essentially a hash table with a linked list of keys.
		 */
		
		LinkedHashMap<String, Bill> billsOfCongress = createBillMap(congress);
		System.out.println(congress + "`s " + billsOfCongress.size() + " bills created at " + System.currentTimeMillis());
		

		/*
		 * List of articles
		 */

		LinkedList<Article> articlesOfCongress = createArticleList(congress);
		
		System.out.println(congress + "`s " + articlesOfCongress.size() + " articles created: " + System.currentTimeMillis());		
		
		/*
		 * Adds article hit counts into the bill's data,
		 * Then prints each congress's bill data into its own CSV for future use.
		 */			
		BufferedWriter writer;
		if(isWindows){
			writer = new BufferedWriter(new FileWriter(windowsOutputDirectory + congress + ".csv"));
		}
		else writer = new BufferedWriter(new FileWriter(linuxOutputDirectory + congress + ".csv"));
		Iterator<Bill> billIterator = billsOfCongress.values().iterator();
		//Loop through one Congress worth of bills
		while(billIterator.hasNext()){
			//Loop through one Congress worth of articles
			Bill bill = billIterator.next();
			trimTitles(bill);
//		System.out.println(bill.getBill_id());
//		Iterator<Article> articleIterator = articlesOfCongress.iterator();
//		while(articleIterator.hasNext()){
//			Article article = articleIterator.next();
			Article[] articleArray = articlesOfCongress.toArray(new Article[articlesOfCongress.size()]);
			// omp parallel for
			for(int i = 0; i < articleArray.length; i++){
				Article article = articleArray[i];
				//Runnable r = new MyThread(bill, article);
				//new Thread(r).start();
				//System.out.println(r.toString());
				searchInParallel(bill, article);
			}
			writer.append(bill.toCSV());
		}
		System.out.println("CSV for " + congress + "created: " + System.currentTimeMillis());
		writer.close();
	}

	public static void searchInParallel(Bill bill, Article article) {
		if(JSON_Parser.appropriateMaterialType(article)){
			searchArticle(bill, article);
		}
	}

	private static void searchArticle(Bill bill, Article article) {
		StringBuilder builder = new StringBuilder();
		builder.append(article.getLead_paragraph());
		builder.append(' ');
		builder.append(article.getHeadline().getMain());
		builder.append(' ');
		builder.append(article.getHeadline().getPrint_headline());
		builder.append(' ');
		builder.append(article.getArticleAbstract());	
		
		Searcher searcher = new Searcher(builder.toString());
		Bill.Title[] titles = bill.getTitles();
		boolean anyTitleStrictHit = false;
		boolean anyTitleMediumHit = false;
		boolean anyTitleEasyHit = false;
		boolean controversial = searcher.getControversial();
		for (int i = 0; i < titles.length; i++){
			String stringTitle = titles[i].getTitle();
			//Using a search where it must match the whole string,
			//but disregarding capitalization, punctuation, and stop words.
			boolean strictHit = searcher.wordsMatchInOrder(stringTitle);
			//all words, any order
			boolean mediumHit = searcher.wordsMatch(stringTitle);
			//most words, any order
			boolean easyHit = searcher.percentWordsMatch(stringTitle, 0.8);
			if(strictHit){
				anyTitleStrictHit = true;
				break;
			}
			if(mediumHit){
				anyTitleMediumHit = true;
			}
			if(easyHit){
				anyTitleEasyHit = true;
			}
		}
		if(anyTitleStrictHit){
			bill.setArticleCount(bill.getArticleCount() + 1);
			if(controversial){
				bill.setArticleControversyCount(bill.getArticleControversyCount() + 1);
			}
		}
		if(anyTitleMediumHit){
			bill.setMoreRelaxedArticleCount(bill.getMoreRelaxedArticleCount() + 1);
			if(controversial){
				bill.setMoreRelaxedArticleControversyCount(bill.getMoreRelaxedArticleControversyCount() + 1);
			}
		}
		if(anyTitleEasyHit){
			bill.setMostRelaxedArticleCount(bill.getMostRelaxedArticleCount() + 1);
			if(controversial){
				bill.setMostRelaxedArticleControversyCount(bill.getMostRelaxedArticleControversyCount() + 1);
			}
		}
		
	}
	
	private static LinkedList<Article> createArticleList(int congress) throws IOException{
		int year1 = firstYearOfCongress(congress);
		LinkedList<Article> list = new LinkedList<Article>();
		for(int year = year1; year < (year1 + 2); year++)
			for (int month = JANUARY; month <= DECEMBER; month++){
				StringBuilder builder = new StringBuilder();
				if (isWindows){
					builder.append(windowsArticleDirectory);
				}
				else builder.append(linuxArticleDirectory);
				builder.append(articleFilename);
				builder.append(year);
				builder.append(month);
				builder.append(".txt");
				String filename = builder.toString();
				try{
					list.addAll(JSON_Parser.parseJsonIntoArticleList(filename));
				}
				catch(NullPointerException e){
					System.err.println("Null pointer exception for " + filename);
				}
			}
		System.out.println("Articles for congress " + congress + " created.");
		return list;
	}
	
	private static LinkedHashMap<String, Bill> createBillMap(int congress){
		StringBuilder builder = new StringBuilder();
		if(isWindows){
			builder.append(windowsBillDirectory);
			builder.append(congress);
			builder.append("\\bills\\");
		}
		else {
			builder.append(linuxBillDirectory);
			builder.append(congress);
			builder.append("/bills/");
		}
		String baseFilepath = builder.toString();
		LinkedHashMap<String, Bill> billMap = new LinkedHashMap<String, Bill>();
		
		//For house
		billMap = addBillsFromChamber(billMap, Bill.Chamber.hr, baseFilepath);
		billMap = addBillsFromChamber(billMap, Bill.Chamber.s, baseFilepath);
		
		System.out.println("Bill Map " + congress + " added.");
		
		return billMap;
	}

	private static LinkedHashMap<String, Bill> addBillsFromChamber(LinkedHashMap<String, Bill> billMap, Bill.Chamber chamber, String baseFilepath) {
		String finalFilepath = null;
		try{
			String houseFilepath;
			if(isWindows){
				houseFilepath = baseFilepath + chamber.toString() + "\\";
			}
			else houseFilepath = baseFilepath + chamber.toString() + "/";
			File houseDirectory = new File(houseFilepath);
			String[] inHouseDirectory = houseDirectory.list();
			//Numbering starts at 1
			for (int i = 1; i <= inHouseDirectory.length; i++){
				StringBuilder buildFinalFile = new StringBuilder();
				buildFinalFile.append(houseFilepath);
				buildFinalFile.append(chamber);
				buildFinalFile.append(i);
				if(isWindows){
					buildFinalFile.append("\\data.json");	
				}
				else buildFinalFile.append("/data.json");
				finalFilepath = buildFinalFile.toString();
				Bill bill = JSON_Parser.parseJsonIntoBill(finalFilepath);
				addBillNumberAsTitle(bill);
				billMap.put(bill.getBill_id(), bill);
			}
		}
		catch(IOException e){
			System.err.println(finalFilepath + " not found.");
		}
		catch(Exception e1){
			System.err.println("It me. I'm broke. " + finalFilepath);
		}
		return billMap;
	}

	private static void addBillNumberAsTitle(Bill bill) {
		Bill.Title[] billNumberArray = new Bill.Title[1];
		Bill.Title billNumber = new Bill.Title();
		billNumber.setTitle(bill.formalBillNumber());
		billNumber.setAs("number");
		billNumber.setType("number");
		billNumber.setIs_for_portion(false);
		bill.addMoreTitles(billNumberArray);
	}
	
	private static int firstYearOfCongress(int congress){
		return 1789 + (congress - 1) * 2;
	}
	
	private static void trimTitles(Bill bill){
		Bill.Title[] array = bill.getTitles();
		ArrayList<Title> list = new ArrayList<Title>();
		int i;
		for (i = 0; i < array.length; i++){
			list.add(array[i]);
		}
		list.removeAll(Arrays.asList(null,""));
		Iterator<Title> iterator = list.iterator();
		while(iterator.hasNext()){
			if(iterator.next().getTitle() == null){
				iterator.remove();
			}
		}
		Bill.Title[] toReturn = new Bill.Title[list.size()];
		toReturn = list.toArray(toReturn);
		bill.setTitles(toReturn);
	}

}

