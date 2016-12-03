package ArticleFetcher;

import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class NYT_ArchiveAPI_Puller {
	
	private static final int DECEMBER = 12;
	private static final int JANUARY = 1;
	private static int CURRENT_YEAR = 2016;
	private static int FIRST_FULLTEXT_YEAR = 1981;
	private static int FIRST_GOVTRACK_YEAR = 1973;
	private static int FIRST_YEAR = 1851;
	private static String apiURLbase = "http://api.nytimes.com/svc/archive/v1/";
	private static String apiURLend = ".json?api-key=";
	private static String apiKey = "76d2d33e9c6e41fa9de677e72ec71b66";
	private static String directory = "C:\\GovTrackData\\ArticleBillDatabase\\ArticleBillDatabase\\NYT_raw\\";
	private static String name = "NYTarchive_";
	//private static int[] years = {1973, 1974, 2013, 2014};
	//private static int[] years = {1981, 1982};
	
	private static String urlBuilder(int year, int month){
		validateDate(year, month);
		StringBuilder builder = new StringBuilder();
		builder.append(apiURLbase);
		builder.append(year);
		builder.append("/");
		builder.append(month);
		builder.append(apiURLend);
		builder.append(apiKey);	
		return builder.toString();
	}
	
	private static void writeURLtoFile(int year, int month, String fileType) throws IOException{
		validateDate(year, month);
		//String filename = directory + name + String.valueOf(year) + String.valueOf(month) + ".txt";
		String filename = directory + name + String.valueOf(year) + String.valueOf(month) + fileType;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		URL url = new URL(urlBuilder(year, month));
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String line;
		while((line = reader.readLine()) != null){
			writer.append(line);
		}
		
		reader.close();
		writer.close();
	}
	
	private static void validateDate(int year, int month){
		if (month < JANUARY | month > DECEMBER){
			throw new IllegalArgumentException("Months should range from 1 to 12.");
		}
		if (year < FIRST_YEAR | year > CURRENT_YEAR){
			throw new IllegalArgumentException("Years should be between " + String.valueOf(FIRST_YEAR) + " and " + String.valueOf(CURRENT_YEAR));
		}
		if (year < FIRST_FULLTEXT_YEAR){
//			System.err.println("NOTE: Results from this year may not have full text. Try 1981 or after.\n");
		}
	}

	public static void main(String[] args) {
		//TODO: Should take args
		try{
			//for(int yearIndex = 0; yearIndex < years.length; yearIndex++){
	/*		for(int year = 1979; year < FIRST_FULLTEXT_YEAR; year++){
				for(int month = JANUARY; month <= DECEMBER; month++){
					//writeURLtoFile(years[yearIndex], month, ".txt");
					//writeURLtoFile(years[yearIndex], month, ".json");
					writeURLtoFile(year, month, ".txt");
					TimeUnit.MILLISECONDS.sleep(300);
				}
			} */
			writeURLtoFile(1985, 2, ".txt");
		}
			
		catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		//To test
	
		/*
		
		try{
			writeURLtoFile(1994, 6, ".txt");
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		*/
	}

}
