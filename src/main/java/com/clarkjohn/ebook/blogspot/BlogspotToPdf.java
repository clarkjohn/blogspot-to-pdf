package com.clarkjohn.ebook.blogspot;

import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.html.BlogspotScraper;
import com.clarkjohn.ebook.blogspot.pdf.BlogspotPDFGenerator;

/**
 * Convert a blogspot blog(s) to a pdf
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogspotToPdf {


	public static void convertBlogPagesToPdf(BlogspotProperties blogspotProperties) {
		
		try {
			System.out.println("Converting blogger urls to PDF");
			
			System.out.println("Scraping blogger html pages using properties=" + blogspotProperties);
			Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages = BlogspotScraper.getSectionTitleToBlogPages(blogspotProperties);
			
			System.out.println("Creating blogger PDF from sectionTitleToBlogPages=" + sectionTitleToBlogPages);
			BlogspotPDFGenerator.createPdf(sectionTitleToBlogPages, blogspotProperties);
			
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
