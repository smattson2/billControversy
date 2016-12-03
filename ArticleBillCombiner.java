package ArticleFetcher;


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
	
	private static String billDirectory = "C:\\GovTrackData\\govTrackJsons\\";
	private static String articleDirectory = "C:\\GovTrackData\\ArticleBillDatabase\\ArticleBillDatabase\\NYT_raw\\";
	private static String articleFilename = "NYTarchive_";
	private static String outputDirectory = "C:\\GovTrackData\\output\\";
	

	public static void main(String[] args) {
		try{
	//	int congress = Integer.valueOf(args[0]).intValue();
			
			//Temp values
	for(int congress = /* FIRST_GOVTRACK_CONGRESS */ 98; congress <=  108; congress++){
			
			/*
			 * Iterable hash map.
			 * This data structure chosen for the ability to quickly find bills by key (for merging)
			 * and also its iterability. It is essentially a hash table with a linked list of keys.
			 */
			
			LinkedHashMap<String, Bill> billsOfCongress = createBillMap(congress);
			System.out.println(congress + "`s " + billsOfCongress.size() + " bills created at " + System.currentTimeMillis());
			
	/*		
			 * Merges bills where one supersedes another, or one is included in another.
			 

			Set<Map.Entry<String, Bill>> set = billsOfCongress.entrySet();
			//Copies titles of bill to delete into bill to keep.
			//Flag all bills to be deleted on next iteration.
			Iterator<Map.Entry<String, Bill>> iterator = set.iterator();
			while(iterator.hasNext()){
				Bill bill = iterator.next().getValue();
				System.out.println(bill.getRelated_bills()[0].getReason());
				RelatedBillMerger merger = new RelatedBillMerger(bill, billsOfCongress);
				merger.mergeRelatedBills();
			}
			
			//A second iteration is required to delete the bills because of the limitations of
			//the data structure. Removing items from the map during iteration without using
			//iterator.remove() causes unpredictable behavior.
			//While this seems at first glance to be an oddly inefficient solution, as it loops twice,
			//it is algorithmically faster than most other solutions because accessing a bill's
			//related bills is O(1), and iteration is O(n).
			Iterator<Map.Entry<String, Bill>> iterator2 = set.iterator();
			while(iterator2.hasNext()){
				Bill bill = iterator2.next().getValue();
				if (bill.getShouldRemove()){
					System.out.println("Removing.");
					iterator2.remove();
				}
			}
			
			System.out.println(congress + "`s bills merged into " + billsOfCongress.size() + " bills at " + System.currentTimeMillis());
*/
			/*
			 * List of articles
			 */

			LinkedList<Article> articlesOfCongress = createArticleList(congress);
			
			System.out.println(congress + "`s " + articlesOfCongress.size() + " articles created: " + System.currentTimeMillis());
			
			/*
			 * Adds article hit counts into the bill's data,
			 * Then prints each congress's bill data into its own CSV for future use.
			 */			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputDirectory + congress + ".csv"));
			Iterator<Bill> billIterator = billsOfCongress.values().iterator();
						//Loop through one Congress worth of bills
			while(billIterator.hasNext()){
				//Loop through one Congress worth of articles
				Bill bill = billIterator.next();
				trimTitles(bill);
		//		System.out.println(bill.getBill_id());
				Iterator<Article> articleIterator = articlesOfCongress.iterator();
				while(articleIterator.hasNext()){
					Article article = articleIterator.next();
					if(JSON_Parser.appropriateMaterialType(article)){
						searchArticle(bill, article);
					}
				}
				writer.append(bill.toCSV());
			}
			System.out.println("CSV for " + congress + "created: " + System.currentTimeMillis());
			writer.close();

			
			System.out.println("Done: " + System.currentTimeMillis());
		}
		}
		catch(Exception e){
			e.printStackTrace();
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
				builder.append(articleDirectory);
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
		builder.append(billDirectory);
		builder.append(congress);
		builder.append("\\bills\\");
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
			String houseFilepath = baseFilepath + chamber.toString() + "\\";
			File houseDirectory = new File(houseFilepath);
			String[] inHouseDirectory = houseDirectory.list();
			//Numbering starts at 1
			for (int i = 1; i <= inHouseDirectory.length; i++){
				StringBuilder buildFinalFile = new StringBuilder();
				buildFinalFile.append(houseFilepath);
				buildFinalFile.append(chamber);
				buildFinalFile.append(i);
				buildFinalFile.append("\\data.json");	
				finalFilepath = buildFinalFile.toString();
				Bill bill = JSON_Parser.parseJsonIntoBill(finalFilepath);
				addBillNumberAsTitle(bill);
				billMap.put(bill.getBill_id(), bill);
			}
		}
		catch(IOException e){
			System.err.println(finalFilepath + " not found.");
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

