package com.clarkjohn.ebook.blogspot.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.BlogspotUrlProperties;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.parser.XMLParser;

/**
 * Create a Blogger comments PDF section
 * 
 * @author john@clarkjohn.com
 *
 */
public class CommentsSectionPdfWriter {

	private CommentsSectionPdfWriter() {	
	}

	static void createCommentsSection(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages, XMLParser xmlParser,
			Document document, PdfWriter writer) throws IOException {

		int count = 0;
		for (Map.Entry<String, List<BlogspotUrlProperties>> entry : sectionTitleToBlogPages.entrySet()) {

			String sectionTitle = entry.getKey();
			document.newPage();
			xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + sectionTitle + "</h3>").getBytes()));

			PdfOutline parentOutline = new PdfOutline(writer.getRootOutline(),
					new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), "Comments: " + sectionTitle, true);

			for (BlogspotUrlProperties blogPage : entry.getValue()) {

				try {
					System.out.println("Creating comment section " + count++ + "=" + blogPage.getBlogTitle());

					document.add(new Chunk(new LineSeparator()));
										
					new PdfOutline(parentOutline, new PdfDestination(PdfDestination.FITB, writer.getVerticalPosition(true)), blogPage.getBlogTitle(),
							true);
					
					xmlParser.parse(new ByteArrayInputStream(new String("<br /><h4>" + blogPage.getBlogTitle() + "</h4>").getBytes()));
					document.add(getCommentsAnchorTargetingBlogPage(blogPage, true));
					xmlParser.parse(new ByteArrayInputStream(blogPage.getCommentsSection().toString().getBytes()));
					document.add(getCommentsAnchorTargetingBlogPage(blogPage, false));

				} catch (Exception e) {
					e.printStackTrace();
					// keep creating comments
				}
			}
		}
	}

	private static Paragraph getCommentsAnchorTargetingBlogPage(BlogspotUrlProperties blogPage, boolean setAnchorReference) {

		Anchor anchor = new Anchor("[Back to " + blogPage.getBlogTitle() + "]");
		if (setAnchorReference) {			
			anchor.setName(blogPage.getPdfCommentsAnchor());
		}
		anchor.setReference("#" + blogPage.getPdfBlogAnchor());

		Paragraph paragraph = new Paragraph();
		paragraph.add(anchor);

		return paragraph;
	}

}
