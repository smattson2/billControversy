package ArticleFetcher;

import com.google.gson.annotations.SerializedName;

public class Bill {
	
	private String bill_id;
	@SerializedName("bill_type")
	private Chamber chamber;
	private int number;
	private int congress;
	private String introduced_at;
	private Title[] titles;
	private String subject_top_term;
	private String[] subjects;
	private String status;
	private RelatedBill[] related_bills;
	
	//Added after--not from the bill JSONs
	private transient int articleCount;
	private transient int articleControversyCount;
	private transient int moreRelaxedArticleCount;
	private transient int moreRelaxedArticleControversyCount;
	private transient int mostRelaxedArticleCount;
	private transient int mostRelaxedArticleControversyCount;
	
	
	private transient boolean shouldRemove = false;
	
	public Bill(){
	}

	public String toCSV(){
		StringBuilder builder = new StringBuilder();
		builder.append(bill_id);
		builder.append(',');
		builder.append(chamber.toString());
		builder.append(',');
		builder.append(number);
		builder.append(',');
		builder.append(congress);
		builder.append(',');
		builder.append(introduced_at);
		builder.append(',');
		//Pipe delimited so titles all stay together in one column
		//builder.append(titles.toString());
		//TODO: add back in. Commas messing up csv.
/*		for(int i = 0; i < titles.length; i++){
			builder.append(titles[i].getTitle());
			builder.append("|");
		}
		builder.append(',');
*/
		builder.append(status);
		builder.append(',');
		builder.append(enacted());
		builder.append(',');
		builder.append(passed());
		builder.append(',');
		builder.append(subject_top_term);
		builder.append(',');
		//TODO: add back in.
/*		
		for(int i = 0; i < subjects.length; i++){
			builder.append(subjects[i]);
			builder.append('|');
		}
		builder.append(',');
*/

		builder.append(articleCount);
		builder.append(',');
		builder.append(articleControversyCount);
		builder.append(',');
		builder.append(moreRelaxedArticleCount);
		builder.append(',');
		builder.append(moreRelaxedArticleControversyCount);
		builder.append(',');
		builder.append(mostRelaxedArticleCount);
		builder.append(',');
		builder.append(mostRelaxedArticleControversyCount);
		builder.append(System.lineSeparator());
		
		return builder.toString();
	}
	
	public String formalBillNumber(){
		if(chamber.equals(Chamber.hr)){
			return "H. R. " + number;
		}
		else if(chamber.equals(Chamber.s)){
			return "S. " + number;
		}
		else throw new IllegalStateException("Chamber must be hr or s!");
	}
	
	//This returns an int instead of a boolean because it is going immediately to Stata.
	//I'm sorry, Sir CodeComplete and The Liberator. I really am. But Stata doesn't have booleans.
	//And it seemed silly to collect a boolean here and immediately transform it to an int.
	
	/**
	 * @return Whether the bill was enacted into law.
	 */
	
	public int enacted(){
		if (status.contains("ENACTED")){
			return 1;
		}
		else return 0;
	}
	
	//This returns an int instead of a boolean because it is going immediately to Stata.
	//I'm sorry, Sir CodeComplete and The Liberator. I really am. But Stata doesn't have booleans.
	//And it seemed silly to collect a boolean here and immediately transform it to an int.
	
	/**
	 * @return Whether the bill was passed by both houses of congress. Includes enacted and vetoes.
	 */
	
	public int passed(){
		if(enacted() == 1){
			return 1;
		}
		if (status.equals("PASSED:BILL")){
			return 1;
		}
		if (status.contains("VETO")){
			return 1;
		}	
		else return 0;
	}
	
	public String getSubject_top_term(){
		return subject_top_term;
	}
	
	public void setSubject_top_term(String subject_top_term){
		this.subject_top_term = subject_top_term;
	}
	
	public String[] getSubjects(){
		return subjects;
	}
	
	public void setSubjects(String[] subjects){
		this.subjects = subjects;
	}
	
	public String getBill_id() {
		return bill_id;
	}

	public void setBill_id(String bill_id) {
		this.bill_id = bill_id;
	}
	
	public Chamber getChamber(){
		return this.chamber;
	}
	
	public void setChamber(Chamber chamber){
		this.chamber = chamber;
	}
	

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


	public int getCongress() {
		return congress;
	}

	public void setCongress(int congress) {
		this.congress = congress;
	}


	public String getIntroduced_at() {
		return introduced_at;
	}

	public void setIntroduced_at(String introduced_at) {
		this.introduced_at = introduced_at;
	}


	public Title[] getTitles() {
		return titles;
	}

	public void setTitles(Title[] titles) {
		this.titles = titles;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public RelatedBill[] getRelated_bills() {
		return related_bills;
	}

	public void setRelated_bills(RelatedBill[] related_bills) {
		this.related_bills = related_bills;
	}


	public int getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}


	public int getArticleControversyCount() {
		return articleControversyCount;
	}

	public void setArticleControversyCount(int articleControversyCount) {
		this.articleControversyCount = articleControversyCount;
	}


	public enum Chamber{
		hr, s;
		
	}
	
	public void addMoreTitles(Title[] moreTitles){
		int oldTitlesLength = titles.length;
		int newTitlesLength = moreTitles.length;
		Title[] combinedTitles = new Title[oldTitlesLength + newTitlesLength];
		for(int i = 0; i < oldTitlesLength; i++){
			combinedTitles[i] = titles[i];
		}
		for(int j = oldTitlesLength; j < (oldTitlesLength + newTitlesLength); j++){
			combinedTitles[j] = moreTitles[j - oldTitlesLength];
		}
		this.titles = combinedTitles;
	}
	
	public boolean getShouldRemove() {
		return shouldRemove;
	}

	public void setShouldRemove(boolean shouldRemove) {
		this.shouldRemove = shouldRemove;
	}
/*
	public static class Titles{
		private String official_title;
		private String popular_title;
		private String short_title;
		private Title[] allTitles;
		
		//Separates titles by pipe so they all stay together in one column in the CSV
		//But are easy to split later if desired
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < allTitles.length; i++){
				builder.append(allTitles[i].getTitle());
				builder.append('|');
			}
			return builder.toString();
		}

		public String getOfficial_title() {
			return official_title;
		}

		public void setOfficial_title(String official_title) {
			this.official_title = official_title;
		}

		public String getPopular_title() {
			return popular_title;
		}

		public void setPopular_title(String popular_title) {
			this.popular_title = popular_title;
		}

		public String getShort_title() {
			return short_title;
		}

		public void setShort_title(String short_title) {
			this.short_title = short_title;
		}

		public Title[] getAllTitles() {
			return allTitles;
		}

		public void setAllTitles(Title[] allTitles) {
			this.allTitles = allTitles;
		}		
	}
*/

	public int getMoreRelaxedArticleCount() {
		return moreRelaxedArticleCount;
	}

	public void setMoreRelaxedArticleCount(int moreRelaxedArticleCount) {
		this.moreRelaxedArticleCount = moreRelaxedArticleCount;
	}

	public int getMoreRelaxedArticleControversyCount() {
		return moreRelaxedArticleControversyCount;
	}

	public void setMoreRelaxedArticleControversyCount(int moreRelaxedArticleControversyCount) {
		this.moreRelaxedArticleControversyCount = moreRelaxedArticleControversyCount;
	}

	public int getMostRelaxedArticleCount() {
		return mostRelaxedArticleCount;
	}

	public void setMostRelaxedArticleCount(int mostRelaxedArticleCount) {
		this.mostRelaxedArticleCount = mostRelaxedArticleCount;
	}

	public int getMostRelaxedArticleControversyCount() {
		return mostRelaxedArticleControversyCount;
	}

	public void setMostRelaxedArticleControversyCount(int mostRelaxedArticleControversyCount) {
		this.mostRelaxedArticleControversyCount = mostRelaxedArticleControversyCount;
	}

	public static class Title{
		private String as;
		private boolean is_for_portion;
		private String title;
		private String type;

		public String getAs() {
			return as;
		}

		public void setAs(String as) {
			this.as = as;
		}

		public boolean getIs_for_portion() {
			return is_for_portion;
		}

		public void setIs_for_portion(boolean is_for_portion) {
			this.is_for_portion = is_for_portion;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	public static class RelatedBill{
		private String bill_id;
		private String reason; //would be enum, but contains forbidden characters
		private String type; //type equals bill is all we want
		public String getBill_id() {
			return bill_id;
		}
		public void setBill_id(String bill_id) {
			this.bill_id = bill_id;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}

}
