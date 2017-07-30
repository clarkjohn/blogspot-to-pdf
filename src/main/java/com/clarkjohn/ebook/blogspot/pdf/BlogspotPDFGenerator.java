package com.clarkjohn.ebook.blogspot.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.clarkjohn.ebook.blogspot.BlogspotUrlProperties;
import com.clarkjohn.ebook.blogspot.BlogspotProperties;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

/**
 * Create a PDF using Itext
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogspotPDFGenerator {

	private Font mainFont;
	private XMLWorkerFontProvider fontProvider;
	private BlogspotProperties blogspotProperties;
	
	public BlogspotPDFGenerator(BlogspotProperties blogspotProperties) {
		
		this.blogspotProperties = blogspotProperties;
		
		fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
		fontProvider.register("Bookerly-Regular.ttf", "Bookerly-Regular");
		fontProvider.register("Bookerly-Italic.ttf", "Bookerly-Italic");

		mainFont = fontProvider.getFont("Bookerly-Regular");
	}


	public void createPdf(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages) throws DocumentException, IOException {
		
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(initOutputFile(), false));
		document.open();

		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

		// HTML
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		htmlContext.setImageProvider(new Base64ImageProvider());

		InputStream csspathtest = Thread.currentThread().getContextClassLoader().getResourceAsStream("blogspot-pdf.css");
		CssFile cssfiletest = XMLWorkerHelper.getCSS(csspathtest);
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		cssResolver.addCss(cssfiletest);

		// Pipelines
		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);

		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

		XMLWorker worker = new XMLWorker(css, true);
		XMLParser xmlParser = new XMLParser(worker);

		xmlParser.parse(new ByteArrayInputStream(new String("<body>").getBytes()));
		createBlogSection(sectionTitleToBlogPages, xmlParser, document);
		createCommentsSection(sectionTitleToBlogPages, xmlParser, document);
				
		document.close();
	}


	private File initOutputFile() {
		
		File outputFolder = new File("output");
		outputFolder.mkdir();
		System.out.println("Creating output file at=" + outputFolder.getAbsolutePath() + File.separator + blogspotProperties.getOutputPdfFileName());
		
		return new File(outputFolder, blogspotProperties.getOutputPdfFileName());
	
	}

	
	private void createCommentsSection(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages, XMLParser xmlParser,
			com.itextpdf.text.Document document) throws IOException {

		int count = 0;
		for (Map.Entry<String, List<BlogspotUrlProperties>> entry : sectionTitleToBlogPages.entrySet()) {

			document.newPage();
			xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + entry.getKey() + "</h3>").getBytes()));

			for (BlogspotUrlProperties blogPage : entry.getValue()) {

				try {
					System.out.println("Creating comment section " + count++ +"=" + blogPage.getBlogTitle());

					document.add(new Chunk(new LineSeparator()));
					xmlParser.parse(new ByteArrayInputStream(new String("<br /><h4>" + blogPage.getBlogTitle() + "</h4>").getBytes()));
					xmlParser.parse(new ByteArrayInputStream(blogPage.getCommentsSection().toString().getBytes()));
					document.add(getCommentsAnchorTargetingBlogPage(blogPage));

				} catch (Exception e) {					
					e.printStackTrace();
					//keep creating comments
				}
			}
		}
	}

	private Paragraph getCommentsAnchorTargetingBlogPage(BlogspotUrlProperties blogPage) {
		
		Anchor anchor = new Anchor("[Back to " + blogPage.getBlogTitle() + "]");
		anchor.setName(blogPage.getPdfCommentsAnchor());
		anchor.setReference("#" + blogPage.getPdfBlogAnchor());
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(anchor);
		
		return paragraph;
	}

	private void createBlogSection(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages, XMLParser xmlParser,
			com.itextpdf.text.Document document) throws IOException {

		int count = 0;
		for (Map.Entry<String, List<BlogspotUrlProperties>> entry : sectionTitleToBlogPages.entrySet()) {

			document.newPage();
			xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + entry.getKey() + "</h3>").getBytes()));

			for (BlogspotUrlProperties blogPage : entry.getValue()) {
				try {
					System.out.println("Creating blog section " + count++ + "=" + blogPage.getBlogTitle());

					//headers
					document.add(new Chunk(new LineSeparator()));
					xmlParser.parse(new ByteArrayInputStream(new String("<h3>" + blogPage.getBlogTitle() + "</h3>").getBytes()));
					xmlParser.parse(new ByteArrayInputStream(new String("<b>" + blogPage.getBlogTextDate() + "</b><br />").getBytes()));
					
					//back to blog
					document.add(getAnchorTargetingCommentsPage(blogPage));
					xmlParser.parse(new ByteArrayInputStream(new String("<br />").getBytes()));
					
					//body
					xmlParser.parse(new ByteArrayInputStream(blogPage.getBlogSection().toString().getBytes()));
					
					//back to blog
					document.add(getAnchorTargetingCommentsPage(blogPage));

				} catch (Exception e) {
					e.printStackTrace();
					//keep creating blog pages
				}
			}
		}
	}

	private Paragraph getAnchorTargetingCommentsPage(BlogspotUrlProperties blogPage) {
		
		Anchor anchor = new Anchor("[comments]");
		anchor.setName(blogPage.getPdfBlogAnchor());
		anchor.setReference("#" + blogPage.getPdfCommentsAnchor());

		Paragraph paragraph = new Paragraph();
		paragraph.setFont(mainFont);
		paragraph.add(anchor);
		
		return paragraph;
	}

}
