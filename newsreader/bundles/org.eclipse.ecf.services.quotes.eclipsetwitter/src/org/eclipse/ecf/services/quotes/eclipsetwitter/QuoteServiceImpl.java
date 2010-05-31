/*******************************************************************************
 *  Copyright (c) 2009 Remain Software, Industrial-TSI & Weltevree Beheer
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Wim Jongman - initial API and implementation
 *
 *
 *******************************************************************************/
package org.eclipse.ecf.services.quotes.eclipsetwitter;

import java.util.Random;

import org.eclipse.ecf.services.quotes.QuoteService;

public class QuoteServiceImpl implements QuoteService {
	final static String NL = System.getProperty("line.separator");

	public static final String[] QUOTES = new String[] {
			"The net knows all! " + NL + "Jeff McAffer - EclipseSource",
			"is building, testing, building (but with hand crafted bits)" + NL
					+ "\tPaul Webster",
			"is sitting still" + NL + "\tPaul Webster",
			"Taking a shower. Need a couple of good ideas. ;-)" + NL
					+ "\tBoris Bokowski",
			"Pressure makes diamonds" + NL + "\tChris Anyszcyk",
			"In vacation. May the internet survive without me" + NL
					+ "\tLars Vogel",
			"BONG BONG BONG BONG" + NL + "Big Ben Clock",
			"wondering if I will ever again give a talk that I didn't prepare up to the minute..."
					+ NL + "\tPeter Friese",
			"I am all for convention over configuration, the only question is: Whose convention?"
					+ NL + "\tWim Jongman",
			"birdstrike today - pigeon into windscreen at 80km/h " + NL
					+ "- quite a solid thunk " + NL
					+ "- some dusty smears - alfa 156 2.0l wins)" + NL
					+ "\tOison Hurley",
			"learning an important lesson: sometimes the crowd is not that wise"
					+ NL + "\tIan Skerrett",
			"Wow, Twitter just hired their 140th employee. I wonder if they will force themselves to stop there."
					+ NL + "\tMik Kersten" };

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
