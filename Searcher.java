package ArticleFetcher;

import java.util.Iterator;
import java.util.List;

public class Searcher {
	
	private final String text;
	private final String cleanText;
	private String[] controversyWords;
	private String[] stopWords;
	private boolean controversial;

	public Searcher(String text){
		this.text = text;
		setStopWords();
		setControversyWords();
		this.cleanText = clean(text);
		setControversial();
	}
	
	private final void setControversyWords(){
		this.controversyWords = new String[15];
		controversyWords[0] = "divid";
		controversyWords[1] = "divisi";
		controversyWords[2] = "disagree";
		controversyWords[3] = "polariz";
		controversyWords[4] = "repeal";
		controversyWords[5] = "stalemate";
		controversyWords[6] = "gridlock";
		controversyWords[7] = "standstill";
		controversyWords[8] = "veto";
		controversyWords[9] = "filibuster";
		controversyWords[10] = "dysfunct";
		controversyWords[11] = "deadlock";
		controversyWords[12] = "ideological difference";
		controversyWords[13] = "budget battle";
		controversyWords[14] = "budgetary battle";
		//Delay/oppose bill removed for simplicity		
	}
	
	private final void setStopWords(){
		//Google stop words
		this.controversyWords = new String[31];
		stopWords[0] = "i"; 
		stopWords[1] = "a"; 
		stopWords[2] = "about"; 
		stopWords[3] = "an"; 
		stopWords[4] = "are"; 
		stopWords[5] = "as"; 
		stopWords[6] = "at"; 
		stopWords[7] = "be"; 
		stopWords[8] = "by"; 
		stopWords[9] = "com"; 
		stopWords[10] = "for"; 
		stopWords[11] = "from";
		stopWords[12] = "how";
		stopWords[13] = "in"; 
		stopWords[14] = "is"; 
		stopWords[15] = "it"; 
		stopWords[16] = "of"; 
		stopWords[17] = "on"; 
		stopWords[18] = "or"; 
		stopWords[19] = "that";
		stopWords[20] = "the"; 
		stopWords[21] = "this";
		stopWords[22] = "to"; 
		stopWords[23] = "was"; 
		stopWords[24] = "what"; 
		stopWords[25] = "when";
		stopWords[26] = "where";
		stopWords[27] = "who"; 
		stopWords[28] = "will"; 
		stopWords[29] = "with";
		stopWords[30] = "www";
	}
	
	private final String clean(String string) {
		String stringChars = string.replace("[\\W&&\\S]+", "");
		String lowercase = stringChars.toLowerCase();
		String noStopWords = lowercase;
		for (int i = 0; i < stopWords.length; i++){
			noStopWords = string.replace(stopWords[i], "");
		}
		return noStopWords;
	}
	
	/*
	 * Strictest: must be an exact string match
	 */
	public boolean strictMatch(String string){
		return text.contains(string);
	}
	
	/*
	 * Very strict: must be an exact match, except for capitalization and punctuation
	 */
	public boolean wordsMatchInOrder(String string){
		String cleanString = clean(string);
		return cleanText.contains(cleanString);
	}
	
	/*
	 * Pretty strict: must match every word
	 */
	public boolean wordsMatch(String string){
		String cleanString = clean(string);
		String[] words = cleanString.split(" ");
		for(int i = 0; i < words.length; i++){
			if(!cleanText.contains(words[i])){
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Variable strictness: must match x% of words.
	 */
	public boolean percentWordsMatch(String string, double percent){
		String cleanString = clean(string);
		String[] words = cleanString.split(" ");
		int count = 0;
		for(int i = 0; i < words.length; i++){
			if(cleanText.contains(words[i])){
				count++;
			}
		}
		return (count / words.length) >= percent;
	}
	
	/*
	 * Must be a wordsMatchInOrder match with one element of the list
	 */
	public boolean matchInList(List<String> list){
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()){
			if(cleanText.contains(iterator.next())){
				return true;
			}
		}
		return false;
	}
	
	//Must match one of the words with a word character match
	private final void setControversial(){
		for (int i = 0; i < controversyWords.length; i++){
			if(cleanText.contains(controversyWords[i])){
				this.controversial = true;
				break;
			}
		}
		this.controversial = false;
	}
	
	public boolean getControversial(){
		return this.controversial;
	}
	
}
