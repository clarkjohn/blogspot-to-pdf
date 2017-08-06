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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
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

	private BlogspotPDFGenerator() {
	}

	public static void createPdf(Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages, BlogspotProperties blogspotProperties)
			throws DocumentException, IOException {

		// fonts
		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
		fontProvider.register("Bookerly-Regular.ttf", "Bookerly-Regular");
		fontProvider.register("Bookerly-Italic.ttf", "Bookerly-Italic");

		// init document
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(initOutputFile(blogspotProperties), false));
		document.open();

		// html
		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.autoBookmark(false);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

		InputStream csspathtest = Thread.currentThread().getContextClassLoader().getResourceAsStream("blogspot-pdf.css");
		CssFile cssfiletest = XMLWorkerHelper.getCSS(csspathtest);
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		cssResolver.addCss(cssfiletest);

		// Pipelines
		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);

		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

		// xmlworker
		XMLWorker worker = new XMLWorker(css, true);
		XMLParser xmlParser = new XMLParser(worker);

		// start writing pdf document
		xmlParser.parse(new ByteArrayInputStream(new String("<body>").getBytes()));
		BlogSectionPdfWriter.createBlogSection(sectionTitleToBlogPages, xmlParser, document, writer, fontProvider.getFont("Bookerly-Regular"));
		CommentsSectionPdfWriter.createCommentsSection(sectionTitleToBlogPages, xmlParser, document, writer);

		document.close();
	}

	private static File initOutputFile(BlogspotProperties blogspotProperties) {

		File outputFolder = new File("output");
		outputFolder.mkdir();
		System.out.println("Creating output file at=" + outputFolder.getAbsolutePath() + File.separator + blogspotProperties.getOutputPdfFileName());

		return new File(outputFolder, blogspotProperties.getOutputPdfFileName());
	}

}
