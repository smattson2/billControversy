package ArticleFetcher;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class Article {
	
	private URL web_url;
	private String snippet;
	private String lead_paragraph;
	@SerializedName("abstract")
	private String articleAbstract;
	//private int print_page;
	//private String[] blog;
	private String source;
	//private String[] multimedia;
	private Headline headline;
	private Keyword[] keywords;
	private String pub_date;
	private String document_type;
	//private String news_desk;
	//private String section_name;
	//private String subsection_name;
	//private Byline byline;
	private String type_of_material;
	private String _id;
	private int word_count;
	//private String slideshow_credits;
	private transient String text;
	
	public String getArticleAbstract(){
		return this.articleAbstract;
	}
	
	public static class Keyword{
		private String value;
		private String isMajor;
		private int rank;
		private String name;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getIsMajor() {
			return isMajor;
		}
		public void setIsMajor(String isMajor) {
			this.isMajor = isMajor;
		}
		public int getRank() {
			return rank;
		}
		public void setRank(int rank) {
			this.rank = rank;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class Headline{
		private String main;
		private String print_headline;
		public String getMain() {
			return main;
		}
		public void setMain(String main) {
			this.main = main;
		}
		public String getPrint_headline() {
			return print_headline;
		}
		public void setPrint_headline(String print_headline) {
			this.print_headline = print_headline;
		}
	}
	
	public Article(){
		
	}

	public URL getWeb_url() {
		return web_url;
	}

	public void setWeb_url(URL web_url) {
		this.web_url = web_url;
	}

	public String getLead_paragraph() {
		return lead_paragraph;
	}

	public void setLead_paragraph(String lead_paragraph) {
		this.lead_paragraph = lead_paragraph;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Headline getHeadline() {
		return headline;
	}

	public void setHeadline(Headline headline) {
		this.headline = headline;
	}

	public String getPub_date() {
		return pub_date;
	}

	public void setPub_date(String pub_date) {
		this.pub_date = pub_date;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public int getWord_count() {
		return word_count;
	}

	public void setWord_count(int word_count) {
		this.word_count = word_count;
	}
	
	@Override
	public String toString(){
		return web_url.toString();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getType_of_material() {
		return type_of_material;
	}

	public void setType_of_material(String type_of_material) {
		this.type_of_material = type_of_material;
	}

	public Keyword[] getKeywords() {
		return keywords;
	}

	public void setKeywords(Keyword[] keywords) {
		this.keywords = keywords;
	}
}
