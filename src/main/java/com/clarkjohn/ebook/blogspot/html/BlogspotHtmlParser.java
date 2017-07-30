package com.clarkjohn.ebook.blogspot.html;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.clarkjohn.ebook.blogspot.BlogspotProperties;
import com.clarkjohn.ebook.blogspot.BlogspotUrlProperties;

/**
 * HTML Parser for blogs on Blogspot.com
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogspotHtmlParser {

	private static final OutputSettings OUTPUT_SETTINGS = new OutputSettings().syntax(OutputSettings.Syntax.xml).charset(StandardCharsets.UTF_8)
			.prettyPrint(true);

	private static final String COLOR_DARK_GREY_HTML = "313237";
	private static final String COLOR_DARK_RED_HTML = "7f0000";
	
	private BlogspotProperties blogspotProperties;

	public BlogspotHtmlParser(BlogspotProperties blogspotProperties) {
		this.blogspotProperties = blogspotProperties;
	}

	public Map<String, List<BlogspotUrlProperties>> getSectionTitleToBlogPages() throws IOException {

		int count = 0;
		Map<String, List<BlogspotUrlProperties>> sectionTitleToBlogPages = new LinkedHashMap<>();
		for (Map.Entry<String, List<String>> entry : blogspotProperties.getBlogSectionToUrls().entrySet()) {

			List<BlogspotUrlProperties> blogPages = new LinkedList<>();
			for (String url : entry.getValue()) {

				Document document = Jsoup.connect(url).get();

				BlogspotUrlProperties blogspotPage = new BlogspotUrlProperties();
				blogspotPage.setUrl(url);
				blogspotPage.setBlogTitle(document.getElementsByClass("post-title entry-title").text());
				blogspotPage.setBlogTextDate(document.getElementsByClass("date-header").text());
				blogspotPage.setBlogSection(parseBlogSection(url));
				blogspotPage.setCommentsSection(parseComments(url));
				blogspotPage.setPdfBlogAnchor("blog" + count);
				blogspotPage.setPdfCommentsAnchor("comments" + count);

				blogPages.add(blogspotPage);
			}

			sectionTitleToBlogPages.put(entry.getKey(), blogPages);
		}

		return sectionTitleToBlogPages;
	}

	public String parseBlogSection(String blogUrl) throws IOException {

		Document blogDocument = Jsoup.connect(blogUrl).get();

		blogDocument.getElementsByClass("comments").remove();
		blogDocument.getElementsByClass("post-feeds").remove();
		blogDocument.getElementsByClass("blog-paper").remove();
		blogDocument.getElementsByClass("post-footer").remove();
		blogDocument.getElementsByClass("blog-pager").remove();
		blogDocument.getElementsByClass("blog-pager").remove();
		blogDocument.getElementsByClass("post-title entry-title").remove();
		blogDocument.getElementsByClass("date-header").remove();

		// remove anything unrelated to blog section
		Elements blogElements = blogDocument.getElementsByClass("column-center-inner").removeClass("post-footer").removeClass("comments")
				.removeClass("comment-form").removeAttr("comment-form").removeClass("post-feeds").removeClass("blog-paper");

		cleanInvalidITextHtml(blogElements);

		String parsedHtml = Jsoup.clean(blogElements.html(), "", Whitelist.relaxed(), OUTPUT_SETTINGS)
				// fix breaks
				.replace("<br>", "<br />")
				// remove spans
				.replace("<span>", "").replace("</span>", "");

		parsedHtml = "<p>" + parsedHtml + "</p>";

		return parsedHtml;
	}

	public String parseComments(String blogUrl) throws IOException {

		Elements commentsElements = Jsoup.connect(blogUrl).get().getElementsByClass("comments");

		commentsElements.select(":containsOwn(Delete)").remove();
		commentsElements.select(":containsOwn(Reply)").remove();
		commentsElements.select(":containsOwn(Replies)").remove();
		commentsElements.select(":containsOwn(Add comment)").remove();
		commentsElements.select(":containsOwn(Load more...)").remove();

		removeLinksFromBlogDate(commentsElements);
		highlistAuthorComments(commentsElements);
		removeInvalidItextTags(commentsElements);
		cleanInvalidITextHtml(commentsElements);

		Whitelist whitelist = Whitelist.basic().addAttributes("p", "style");
		String comments = Jsoup.clean(commentsElements.html(), "", whitelist, OUTPUT_SETTINGS)
				// fix breaks
				.replace("<br>", "<br />")
				// add more spacing between messages
				.replaceAll("</li>", "<br /></li>");

		return comments;
	}

	private void removeInvalidItextTags(Elements elements) {

		for (Element element : elements.select("p")) {
			if (element.text().contains("/")) {
				element.text(element.text().replaceAll("/", ""));
			}
		}
	}

	private void highlistAuthorComments(Elements elements) {
		
		for (Element element : elements.select("cite")) {
			if (containsIgnoreCase(element.text(), blogspotProperties.getCommentatorsToHighlight())) {
				element.wrap("<b><p style=\"color:" + COLOR_DARK_RED_HTML + ";\"></p></b>");
			} else {
				element.wrap("<b><p style=\"color:" + COLOR_DARK_GREY_HTML + ";\"></p></b>");
			}
		}
	}
	
	//TODO remove this
	private boolean containsIgnoreCase(String str, List<String> list) {
	    
		for (String i : list){
	        if(i.equalsIgnoreCase(str))
	            return true;
	    }
		
	    return false;
	}

	private static void removeLinksFromBlogDate(Elements commentsElements) {
		
		for (Element link : commentsElements.select("a[href]")) {

			if (link.text().contains(" at ")) {
				link.wrap("<i></i>");
			}

			link.unwrap();
		}
	}

	private static void cleanInvalidITextHtml(Elements elements) {

		// remove empty tables, itext will crash
		Elements tbodyElements = elements.select("tbody");
		for (Element tbody : tbodyElements) {
			if (tbody.outerHtml().equals("<tbody></tbody>")) {
				tbody.parent().unwrap();
			}
		}
	}

}
