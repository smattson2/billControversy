package ArticleFetcher;

public class Master {

	private static final int FIRST_GOVTRACK_CONGRESS = 93;
	private static final int FIRST_FULLTEXT_CONGRESS = 97;
	private static final int CURRENT_CONGRESS = 114;
	private static final int TEMP_START_CONGRESS = 98;
	private static final int TEMP_END = 108;
	
	public static void main(String[] args) {
		for(int congress = TEMP_START_CONGRESS; congress < TEMP_END; congress++){
			Runnable r = new MyThread(congress);
			new Thread(r).start();
		}

	}

}
