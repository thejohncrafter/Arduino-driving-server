package com.ArduinoDrivingServer.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

/**
 * This class is used to "compress" the created HTML file. It deletes unused whitespaces.
 * 
 * @author thejohncrafter
 *
 */
public class CompressTag implements BodyTag {
	
	/**
	 * This field stores the parent tag (used by <code>getParent()</code>).
	 * @see getParent
	 */
	private Tag parent;
	
	/**
	 * This field stores the <code>BodyContent</code>.
	 */
	private BodyContent bodyContent;
	
	@Override
	public int doAfterBody() throws JspException {
		
		String rawBody = bodyContent.getString();
		String compressedBody = "";
		
		boolean prevWasBlank = false;
		
		for(int i = 0; i < rawBody.length(); i++){
			
			char curr = rawBody.charAt(i);
			
			if((curr == ' ' || curr == '\t' || curr == '\n') && !prevWasBlank){
				
				compressedBody += curr;
				prevWasBlank = true;
				
			}else if(curr != ' ' && curr != '\t' && curr != '\n'){
				
				prevWasBlank = false;
				compressedBody += curr;
				
			}
			
		}
		
		try {
			
			bodyContent.getEnclosingWriter().write(compressedBody);
			
		} catch (IOException e) {
			
			throw new JspException(e);
			
		}
		
		return SKIP_BODY;
		
	}
	
	@Override
	public int doStartTag() throws JspException {return EVAL_PAGE;}
	
	@Override
	public int doEndTag() throws JspException {return EVAL_PAGE;}
	
	@Override
	public void doInitBody() throws JspException {/* Not used yet. */}
	
	@Override
	public void release() {/* Not used yet. */}
	
	@Override
	public void setBodyContent(BodyContent bodyContent) {this.bodyContent = bodyContent;}
	
	@Override
	public void setPageContext(PageContext pageContext) {/* Not used yet. */}
	
	@Override
	public Tag getParent() {return parent;}
	
	@Override
	public void setParent(Tag parent) {this.parent = parent;}
	
}
