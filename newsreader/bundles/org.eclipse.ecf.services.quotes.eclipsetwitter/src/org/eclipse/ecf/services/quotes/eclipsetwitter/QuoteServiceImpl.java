package org.eclipse.ecf.services.quotes.eclipsetwitter;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {
	final static String NL = System.getenv("line.separator");
	
	public static final String[] QUOTES = new String[] { "2+2 = 5 for extremely large values of 2.",
			"The net knows all! " +
			NL + "Jeff McAffer - EclipseSource",
			"is building, testing, building (but with hand crafted bits)",
			NL + "Paul Webster - IBM" };

	private Random random;

	public QuoteServiceImpl() {
		random = new Random(System.nanoTime());
	}

	public String getRandomQuote() {
		return QUOTES[random.nextInt(QUOTES.length)];
	}

	public String getServiceName() {
		return "Eclipse Twitter";
	}

	public String getServiceDescription() {
		return "Eclipse Twitter";
	}

	public String[] getAllQuotes() {
		return QUOTES;
	}

}
