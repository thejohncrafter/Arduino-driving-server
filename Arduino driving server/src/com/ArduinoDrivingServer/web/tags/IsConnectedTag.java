package com.ArduinoDrivingServer.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * This tag is a condition : the content is shown if a user is connected.
 * 
 * @author Julien Marquet
 *
 */
public class IsConnectedTag implements Tag {

	/**
	 * This field stores the parent tag (used by <code>getParent()</code>).
	 * 
	 * @see getParent
	 */
	private Tag parent;

	/**
	 * This field stores the <code>PageContext</code>.
	 */
	private PageContext pageContext;

	/**
	 * This boolean is used to know if the tag is "inverted" (the content is
	 * shown if the session contains any user).
	 */
	private boolean invert;

	@Override
	public int doStartTag() throws JspException {

		if (pageContext.getSession().getAttribute("user") == null) {

			if (!invert)
				return SKIP_BODY;
			else
				return EVAL_BODY_INCLUDE;

		}

		if (!invert)
			return EVAL_BODY_INCLUDE;
		else
			return SKIP_BODY;

	}

	/**
	 * This method is used to set the "invert" boolean.
	 * 
	 * @param val
	 *            The "invert" boolean. Must be "true" or "false".
	 * @throws IllegalArgumentException
	 *             If the given value isn't "true" or "false".
	 */
	public void setInvert(String val) {

		switch (val) {

		case "true":
			invert = true;
			break;
		case "false":
			// invert = false
			break;
		default:
			throw new IllegalArgumentException(
					"invert must be TRUE or FALSE. You given " + val);

		}

	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	@Override
	public void release() { /* Not used yet. */
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	@Override
	public Tag getParent() {
		return parent;
	}

	@Override
	public void setParent(Tag parent) {
		this.parent = parent;
	}

}
