package com.clarkjohn.ebook.blogspot.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.BlogspotUrlProperties;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.parser.XMLParser;

/**
 * Create a Blogger blog section PDF 
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogSectionPdfWriter {

	private BlogSectionPdfWriter() {
	}

	public static void createBlogSection(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages, XMLParser xmlParser,
			com.itextpdf.text.Document document, PdfWriter writer, Font mainFont) throws IOException {

		int count = 0;
		for (Map.Entry<String, List<BlogspotUrlProperties>> entry : sectionTitleToBlogPages.entrySet()) {

			String sectionTitle = entry.getKey();
			document.newPage();
			xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + sectionTitle + "</h3>").getBytes()));

			// create parent bookmark
			PdfOutline parentOutline = new PdfOutline(writer.getRootOutline(),
					new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), sectionTitle, true);

			for (BlogspotUrlProperties blogPage : entry.getValue()) {
				try {
					System.out.println("Creating blog section " + count++ + "=" + blogPage.getBlogTitle());

					// headers
					document.add(new Chunk(new LineSeparator()));

					// create child bookmark
					new PdfOutline(parentOutline, new PdfDestination(PdfDestination.FITB, writer.getVerticalPosition(true)), blogPage.getBlogTitle(),
							true);

					xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + blogPage.getBlogTitle() + "</h3>").getBytes()));
					xmlParser.parse(new ByteArrayInputStream(new String("<b>" + blogPage.getBlogTextDate() + "</b><br />").getBytes()));

					// to comments
					document.add(getAnchorTargetingCommentsPage(blogPage, true, mainFont));

					xmlParser.parse(new ByteArrayInputStream(new String("<br />").getBytes()));

					// body
					xmlParser.parse(new ByteArrayInputStream(blogPage.getBlogSection().toString().getBytes()));

					// to comments
					document.add(getAnchorTargetingCommentsPage(blogPage, false, mainFont));

				} catch (Exception e) {
					e.printStackTrace();
					// keep creating blog pages
				}
			}
		}
	}

	private static Paragraph getAnchorTargetingCommentsPage(BlogspotUrlProperties blogPage, boolean setAnchorReference, Font mainFont) {

		Anchor anchor = new Anchor("[comments]");
		if (setAnchorReference) {
			anchor.setName(blogPage.getPdfBlogAnchor());
		}
		anchor.setReference("#" + blogPage.getPdfCommentsAnchor());

		Paragraph paragraph = new Paragraph();
		paragraph.setFont(mainFont);
		paragraph.add(anchor);

		return paragraph;
	}

}
