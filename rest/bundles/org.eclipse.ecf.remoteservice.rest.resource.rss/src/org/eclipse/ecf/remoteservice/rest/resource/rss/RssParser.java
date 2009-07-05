/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Subinkrishna G - http://javabeanz.wordpress.com/2007/07/25/rss-parser-sax/
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.ecf.remoteservice.rest.resource.rss;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RssParser extends DefaultHandler {
	
    private String rssText;
    private RssFeed rssFeed;
    private StringBuffer text;
    private Item item;
    private boolean imgStatus;
   
    public RssParser(String rssText) {
        this.rssText = rssText;
        text = new StringBuffer();
    }
   
    public void parse() {
        SAXParserFactory spf = null;
        SAXParser sp = null;       
        spf = SAXParserFactory.newInstance();
		if (spf != null) {
		    try {
				sp = spf.newSAXParser();
				sp.parse(new InputSource(new StringReader(rssText)), this);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		} 
    }
    
    public RssFeed getFeed() {
        return rssFeed;
    }
   
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if(qName.equalsIgnoreCase("channel"))
            rssFeed = new RssFeed();
        else if(qName.equalsIgnoreCase("item") && (rssFeed != null)) {
            item = new Item();
            rssFeed.addItem(item);
        } else if(qName.equalsIgnoreCase("image") && (rssFeed != null))
            imgStatus = true;
    }
   
    public void endElement(String uri, String localName, String qName) {
        if(rssFeed == null)
            return;
       
        if(qName.equalsIgnoreCase("item"))
            item = null;
       
        else if(qName.equalsIgnoreCase("image"))
            imgStatus = false;
       
        else if(qName.equalsIgnoreCase("title")) {
            if(item != null) 
            	item.title = text.toString().trim();
            else if(imgStatus) 
            	rssFeed.imageTitle = text.toString().trim();
            else 
            	rssFeed.title = text.toString().trim();
        } else if (qName.equalsIgnoreCase("link")) {
            if(item != null) 
            	item.link = text.toString().trim();
            else if(imgStatus) 
            	rssFeed.imageLink = text.toString().trim();
            else 
            	rssFeed.link = text.toString().trim();
        } else if(qName.equalsIgnoreCase("description")) {
            if(item != null) 
            	item.description = text.toString().trim();
            else 
            	rssFeed.description = text.toString().trim();
        } else if(qName.equalsIgnoreCase("url") && imgStatus)
            rssFeed.imageUrl = text.toString().trim();
       
        else if(qName.equalsIgnoreCase("language"))
            rssFeed.language = text.toString().trim();
       
        else if(qName.equalsIgnoreCase("generator"))
            rssFeed.generator = text.toString().trim();
       
        else if(qName.equalsIgnoreCase("copyright"))
            rssFeed.copyright = text.toString().trim();
       
        else if(qName.equalsIgnoreCase("pubDate") && (item != null))
            item.pubDate = text.toString().trim();
       
        else if(qName.equalsIgnoreCase("category") && (item != null))
            rssFeed.addItem(text.toString().trim(), item);
       
        text.setLength(0);
    }
   
    public void characters(char[] ch, int start, int length) {
        text.append(ch, start, length);
    }
      
  
    public static class RssFeed {
        public String title;
        public String description;
        public String link;
        public String language;
        public String generator;
        public String copyright;
        public String imageUrl;
        public String imageTitle;
        public String imageLink;
       
        private List items;
        private Map category;
       
        public void addItem(Item item)
        {
            if (this.items == null)
                this.items = new ArrayList();
            this.items.add(item);
        }
       
        public void addItem(String category, Item item)
        {
            if(this.category == null)
                this.category = new HashMap();
            if(!this.category.containsKey(category))
                this.category.put(category, new ArrayList());
            ((List) this.category.get(category)).add(item);
        }
    }
   
   
    public static class Item {
        public String title;
        public String description;
        public String link;
        public String pubDate;
       
        public String toString()
        {
            return (title + ": " + pubDate + "n" + description);
        }
    }
   
}
