package com.ArduinoDrivingServer.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.ArduinoDrivingServer.web.beans.User;

/**
 * This tag is a condition : the content is sghown if a user is connected.
 * @author thejohncrafter
 *
 */
public class UsernameTag implements Tag {
	
	/**
	 * This field stores the parent tag (used by <code>getParent()</code>).
	 * @see getParent
	 */
	private Tag parent;
	
	/**
	 * This field stores the <code>PageContext</code>.
	 */
	private PageContext pageContext;
	
	@Override
	public int doStartTag() throws JspException {
		
		try{
			
			pageContext.getOut().write(((User) pageContext.getSession().getAttribute("user")).getName());
			
		}catch(Exception e){
			
			throw new JspException(e);
			
		}
		
		return EVAL_PAGE;
		
	}
	
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
