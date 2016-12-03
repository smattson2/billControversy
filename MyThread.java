package ArticleFetcher;

public class MyThread implements Runnable {
	
	private final int congress;
	
	public MyThread(int congress){
		this.congress = congress;
	}

	@Override
	public void run() {
		String[] args = new String[1];
		args[0] = String.valueOf(congress);
		ArticleBillCombiner.main(args);
	}

}
