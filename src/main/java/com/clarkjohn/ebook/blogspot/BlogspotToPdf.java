package com.clarkjohn.ebook.blogspot;

import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.html.BlogspotHtmlParser;
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
			System.out.println("Parsing blogger html pages");
			Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages = new BlogspotHtmlParser(blogspotProperties).getSectionTitleToBlogPages();
			
			System.out.println("Creating Blogspot PDF");
			new BlogspotPDFGenerator(blogspotProperties).createPdf(sectionTitleToBlogPages);
			
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
