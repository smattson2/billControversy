package ArticleFetcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
//import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

public class JSON_Parser {
	
	private static String directory = "C:\\GovTrackData\\ArticleBillDatabase\\ArticleBillDatabase\\NYT_raw\\";
	private static String name = "NYTarchive_";
	private static String username = "sem129";
	private static String password = "r1taSkeeter";
	
	public static void main(String[] args) {		
		try {

			List<Article> articleList = new LinkedList<Article>();
			int [] years = {1981, 1982, 2013, 2014};	
			for (int i = 0; i < years.length; i++){
				for(int month = 1; month <= 12; month++){
					String filename = directory + name + years[i] + month + ".txt";
				//	System.out.println(filename);
					articleList.addAll(JSON_Parser.<Article>parseJsonIntoArticleList(filename));
				}
			}
			
			Iterator<Article> iterator = articleList.iterator();
			int count = 0;
			while(iterator.hasNext()){
				//URL url = iterator.next().getWeb_url();
				Article next = iterator.next();
		//		System.out.println(next.getSource());
		//		System.out.println(next.getType_of_material());
				if(next.getSource().equals("The New York Times") && appropriateMaterialType(next)){
					count++;
				}
			}
		//	String read = readUrl(articleList.get(0).getWeb_url());
		//	System.out.println(read);
			System.out.println(count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean appropriateMaterialType(Article article) {
		String type = article.getType_of_material();
		ArrayList<String> list = textTypes();
		if (list.contains(type)){
			return true;
		}
		else return false;
	}
	
	private static ArrayList<String> textTypes(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("An analysis");
		list.add("Article");
		list.add("Column");
		list.add("Economic Analysis");
		list.add("Editorial");
		list.add("Front Page");
		list.add("Letter");
		list.add("News");
		list.add("News Analysis");
		list.add("Op-Ed");
		list.add("Quote");
		list.add("Statistics");
		list.add("Summary");
		list.add("Text");
		return list;
	}

	//Articles are in arrays by month.
	public static List<Article> parseJsonIntoArticleList(String filename) throws IOException{
		LinkedList<Article> list = null;
		try{
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)));
			reader.setLenient(true);
			Type type = new TypeToken<LinkedList<Article>>(){}.getType();
			list = gson.fromJson(reader, type);
		//	System.out.println(filename);
			reader.close();
		}
		catch(com.google.gson.JsonSyntaxException e){
			System.err.println(filename + ": JSON irregular");
			System.err.println(e.getMessage());
		}
		finally{
			return list;
		}
	}
	
	//I KNOW this is terrible, terrible form. But I must move on. :'(
	//Bills are in objects by bill.
	public static Bill parseJsonIntoBill(String filename) throws IOException{
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)));
		reader.setLenient(true);
		Type type = new TypeToken<Bill>(){}.getType();
		Bill bill = gson.fromJson(reader, type);
		reader.close();
		return bill;
	}
	
	public static String readUrl (URL url) throws IOException{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		//BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		while ((line = reader.readLine()) != null){
			builder.append(line);
			builder.append("\n");
		}
		reader.close();
		System.err.println(urlConnection.getErrorStream());
		return builder.toString();

	}
	

	
	//********************************************************************
	//Better form but doesn't work
/*	public static <T> List<T> parseJsonIntoList(String filename) throws IOException{
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new BufferedReader(new FileReader(filename)));
		reader.setLenient(true);
		Type type = new TypeToken<LinkedList<T>>(){}.getType();
		LinkedList<T> list = gson.fromJson(reader, type);
		reader.close();
		return list;
	} */

}
