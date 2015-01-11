package com.ArduinoDrivingServer.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.users.Permissions;

/**
 * This tag is used for permissions.
 * 
 * @author thejohncrafter
 *
 */
public class PermissionsTag implements Tag {
	
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
	 * This field stores the minimum permission value.<br>
	 * Note : this field is a <code>Integer</code> because the class 
	 * needs to know if it has been defined (via <code>setMinValue())
	 * @see setMinValue
	 */
	private Integer minVal;
	
	/**
	 * This field stores the permission name.
	 */
	private String permission;
	
	/**
	 * This boolean is used to know if the tag is "inverted" 
	 * (the content is shown if the permission is > than needed).
	 */
	private boolean invert;
	
	@Override
	public int doStartTag() throws JspException {
		
		if(permission == null || minVal == null)
			throw new NullPointerException("Missing permission and/or minimum value !");
		
		User user = (User) pageContext.getSession().getAttribute("user");
		
		if(user != null){
			
			if(Permissions.getPermission(user, permission) >= minVal){
				
				if(!invert)
					return EVAL_BODY_INCLUDE;
				else
					return SKIP_BODY;
				
			}// else{return SKIP_BODY;}
			
		}
		
		if(!invert)
			return SKIP_BODY;
		else
			return EVAL_BODY_INCLUDE;
		
	}
	
	/**
	 * This is a setter for the minimum permission value.<br>
	 * Accepted arguments : <code>NONE, READ, ALL</code>.
	 * @param val The minimum value.
	 * @throws IllegalArgumentException If the given argument isn't one 
	 * of the accepted arguments.
	 */
	public void setMinValue(String val){
		
		switch(val){
		
		case "NONE" :
			minVal = Permissions.NONE;
			break;
		case "READ" :
			minVal = Permissions.READ;
			break;
		case "ALL" :
			minVal = Permissions.ALL;
			break;
		default :
			throw new IllegalArgumentException("minValue must be NONE, READ or ALL. You given " + val);
		
		}
		
	}
	
	/**
	 * This method is used to set the "invert" boolean.
	 * @param val The "invert" boolean. Must be "true" or "false".
	 * @throws IllegalArgumentException If the given value isn't "true" or "false".
	 */
	public void setInvert(String val){
		
		switch(val){
		
		case "true" :
			invert = true;
			break;
		case "false" :
			// invert = false
			break;
		default :
			throw new IllegalArgumentException("invert must be TRUE or FALSE. You given " + val);
		
		}
		
	}
	
	/**
	 * This method is used to set the permission name (called by tag processor).
	 * @param permission The permission name
	 */
	public void setPermission(String permission){this.permission = permission;}
	
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
