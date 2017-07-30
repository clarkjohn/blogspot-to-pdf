package com.clarkjohn.ebook.blogspot;

import java.util.List;
import java.util.Map;

/**
 * Properties about all the blogspot pages when converting a PDF
 * 
 * @author john@clarkjohn.com
 *
 */
public class BlogspotProperties {

	private Map<String, List<String>> blogSectionToUrls;
	private String outputPdfFileName;
	private List<String> commentatorsToHighlight;

	public Map<String, List<String>> getBlogSectionToUrls() {
		return blogSectionToUrls;
	}

	public void setBlogSectionToUrls(Map<String, List<String>> blogSectionToUrls) {
		this.blogSectionToUrls = blogSectionToUrls;
	}

	public String getOutputPdfFileName() {
		return outputPdfFileName;
	}

	public void setOutputPdfFileName(String outputPdfFileName) {
		this.outputPdfFileName = outputPdfFileName;
	}

	public List<String> getCommentatorsToHighlight() {
		return commentatorsToHighlight;
	}

	public void setCommentatorsToHighlight(List<String> commentatorsToHighlight) {
		this.commentatorsToHighlight = commentatorsToHighlight;
	}

	@Override
	public String toString() {
		return "BlogspotProperties [blogSectionToUrls=" + blogSectionToUrls + ", outputPdfFileName=" + outputPdfFileName
				+ ", commentatorsToHighlight=" + commentatorsToHighlight + "]";
	}

}
