package com.clarkjohn.ebook.blogspot;

/**
 * Meta properties about an individual Blogspot url
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogspotUrlProperties {

	private String url;
	private String blogSection;
	private String commentsSection;
	private String blogTitle;
	private String blogTextDate;
	private String pdfBlogAnchor;
	private String pdfCommentsAnchor;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBlogSection() {
		return blogSection;
	}

	public void setBlogSection(String blogSection) {
		this.blogSection = blogSection;
	}

	public String getCommentsSection() {
		return commentsSection;
	}

	public void setCommentsSection(String commentsSection) {
		this.commentsSection = commentsSection;
	}

	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public String getBlogTextDate() {
		return blogTextDate;
	}

	public void setBlogTextDate(String blogTextDate) {
		this.blogTextDate = blogTextDate;
	}

	public String getPdfBlogAnchor() {
		return pdfBlogAnchor;
	}

	public void setPdfBlogAnchor(String pdfBlogAnchor) {
		this.pdfBlogAnchor = pdfBlogAnchor;
	}

	public String getPdfCommentsAnchor() {
		return pdfCommentsAnchor;
	}

	public void setPdfCommentsAnchor(String pdfCommentsAnchor) {
		this.pdfCommentsAnchor = pdfCommentsAnchor;
	}

	@Override
	public String toString() {
		return "BlogspotUrlProperties [url=" + url + ", blogSection=" + blogSection + ", commentsSection=" + commentsSection + ", blogTitle="
				+ blogTitle + ", blogTextDate=" + blogTextDate + ", pdfBlogAnchor=" + pdfBlogAnchor + ", pdfCommentsAnchor=" + pdfCommentsAnchor
				+ "]";
	}

}
