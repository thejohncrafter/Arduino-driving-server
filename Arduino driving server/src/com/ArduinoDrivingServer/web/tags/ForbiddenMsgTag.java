package com.ArduinoDrivingServer.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * This tag shows a "forbidden access" message like this :<br>
 * <div style="border:3px solid black;">
 *  <H1>ACCESS RESTRICTION</H1>
 *	<strong>&lt;message></strong><br>
 * 	Contact your administrator if you think this is an error.<br>
 * 	<a>Back to home</a>
 * </div>
 * @author Julien Marquet
 *
 */
public class ForbiddenMsgTag implements Tag {
	
	/**
	 * This field stores the parent tag (used by <code>getParent()</code>).
	 * @see getParent
	 */
	private Tag parent;
	
	/**
	 * This field stores the <code>PageContext</code>.
	 */
	private PageContext pageContext;
	
	/**
	 * This field stores the message.
	 */
	private String message;
	
	@Override
	public int doStartTag() throws JspException {
		
		JspWriter out = pageContext.getOut();
		
		try {
			
			out.print("<H1>ACCESS RESTRICTION</H1>");
			out.print("<strong>" + message + "</strong><br>");
			out.print("Contact your administrator if you think this is an error.<br>");
			out.print("<a href=\"ADS\">Back to home</a>");
			
		} catch (IOException e) {
			
			throw new JspException(e);
			
		}
		
		return EVAL_PAGE;
		
	}
	
	/**
	 * This method is used to set the message (displayed at the second line).
	 * @param message The message to display.
	 */
	public void setMessage(String message){this.message = message;}
	
	@Override
	public int doEndTag() throws JspException {return EVAL_PAGE;}
	
	@Override
	public void release() { /* Not used yet. */}
	
	@Override
	public void setPageContext(PageContext pageContext) {this.pageContext = pageContext;}
	
	@Override
	public Tag getParent() {return parent;}
	
	@Override
	public void setParent(Tag parent) {this.parent = parent;}
	
}
