package org.eclipse.ecf.services.quotes.eclipsetwitter;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {
	final static String NL = System.getenv("line.separator");

	public static final String[] QUOTES = new String[] {
			"The net knows all! " + NL + "Jeff McAffer - EclipseSource",
			"is building, testing, building (but with hand crafted bits)",
			NL + "Paul Webster",
			"is sitting still",
			NL + "Paul Webster",
			"Taking a shower. Need a couple of good ideas. ;-)",
			NL + "Boris Bokowski",
			"Pressure makes diamonds",
			NL + "Chris Anyszcyk",
			"In vacation. May the internet survive without me",
			NL + "Lars Vogel",
			"BONG BONG BONG BONG",
			NL + "Big Ben Clock",
			"wondering if I will ever again give a talk that I didn't prepare up to the minute...",
			NL + "Peter Friese",
			"I am all for convention over configuration, the only question is: Whose convention?",
			NL + "Wim Jongman",
			"birdstrike today - pigeon into windscreen at 80km/h " + NL
					+ "- quite a solid thunk " + NL
					+ "- some dusty smears - alfa 156 2.0l wins)",
			NL + "Oison Hurley",
			"learning an important lesson: sometimes the crowd is not that wise",
			NL + "Ian Skerrett", };

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
