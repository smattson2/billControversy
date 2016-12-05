package ArticleFetcher;

import static org.junit.Assert.*;

import org.junit.Test;

public class SearcherTest {
	
	private String title = "International Statute Of Wizarding Secrecy";
	private String shouldHardMatch = "Harry Potter violated the International Statute of Wizarding Secrecy.";
	private String crazyButShouldHardMatch = "Muggles shouldn't be able to read about the inTERnat[]!ional www $$$STATute WiZARD**ing (secrecy).";
	private String shouldMediumMatch = "The international wizarding community is divided on the value of the statute of secrecy.";
	private String shouldEasyMatch = "The full title of the law is not the International Statute of Secrecy.";
	//private String controversial = "The wizarding community is divided over whether to repeal the International Statute of Wizarding Secrecy.";

	@Test
	public void shouldHardMatch(){
		Searcher searcher = new Searcher(shouldHardMatch);
		assertTrue(searcher.wordsMatchInOrder(title));
		assertTrue(searcher.wordsMatch(title));
		assertTrue(searcher.percentWordsMatch(title, .5));
		assertFalse(searcher.getControversial());
	}
	
	@Test
	public void crazyButShouldHardMatch(){
		Searcher searcher = new Searcher(crazyButShouldHardMatch);
		assertTrue(searcher.wordsMatchInOrder(title));
		assertTrue(searcher.wordsMatch(title));
		assertTrue(searcher.percentWordsMatch(title, .5));
		assertFalse(searcher.getControversial());
	}
	
	@Test
	public void shouldMediumMatch(){
		Searcher searcher = new Searcher(shouldMediumMatch);
		assertFalse(searcher.wordsMatchInOrder(title));
		assertTrue(searcher.wordsMatch(title));
		assertTrue(searcher.percentWordsMatch(title, .5));
		assertTrue(searcher.getControversial());
	}

	@Test
	public void shouldEasyMatch(){
		Searcher searcher = new Searcher(shouldEasyMatch);
		assertFalse(searcher.wordsMatchInOrder(title));
		assertFalse(searcher.wordsMatch(title));
		assertTrue(searcher.percentWordsMatch(title, .5));
		assertFalse(searcher.getControversial());
	}
	
}
