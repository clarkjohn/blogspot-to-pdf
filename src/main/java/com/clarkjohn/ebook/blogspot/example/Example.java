package com.clarkjohn.ebook.blogspot.example;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.BlogspotProperties;
import com.clarkjohn.ebook.blogspot.BlogspotToPdf;

/**
 * Example program to scrape a Blogger urls and convert to a PDF
 * Real links removed
 * 
 * @author john@clarkjohn.com
 *
 */
public class Example {

	public static void main(String[] args) {
		
		BlogspotProperties blogspotProperties = new BlogspotProperties();
		blogspotProperties.setOutputPdfFileName("example.pdf");
		blogspotProperties.setBlogSectionToUrls(getBlogSectionToUrls());
		blogspotProperties.setCommentatorsToHighlight(Arrays.asList("Anonymous"));
		
		BlogspotToPdf.convertBlogPagesToPdf(blogspotProperties);
	}

	private static Map<String, List<String>> getBlogSectionToUrls() {
		
		Map<String, List<String>> blogNameToUrls = new LinkedHashMap<>();
		List<String> urlList = new LinkedList<>();
		urlList.add("http://?.blogspot.com/...");
		urlList.add("http://?.blogspot.com/...");
		blogNameToUrls.put("Example Section 1", urlList);
		
		List<String> urlList2 = new LinkedList<>();
		urlList2.add("http://?.blogspot.com/...");
		urlList2.add("http://?.blogspot.com/...");
		blogNameToUrls.put("Example Section 2", urlList2);
		
		return blogNameToUrls;
	}

}
