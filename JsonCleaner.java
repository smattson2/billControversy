package ArticleFetcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Should only need to be done once.
 * @author sem129
 *
 */

public class JsonCleaner {
	
	private static String directory = "C:\\GovTrackData\\ArticleBillDatabase\\ArticleBillDatabase\\NYT_raw\\";
	private static String name = "NYTarchive_";
	
	public static void main(String[] args){
		try {
			//TODO: Magic numbers, args
			for (int year = 2014; year <= 2014; year++){
				for(int month = 1; month <= 12; month++)
				cleanJson(directory + name + year + month + ".txt");	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void cleanJson(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null){
			builder.append(line);
			builder.append("\n");
		}
		reader.close();
		int i = 0;
		while(builder.charAt(i) != '['){
			i++;
		}
		int closingBracketFromEnd = 3;
		String toWrite = builder.substring(i, builder.length() - closingBracketFromEnd).toString();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(toWrite);
		writer.close();
	}

}
