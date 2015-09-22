package com.kswist.statistics.utils;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

public class WebLog implements Serializable {

	private static final long serialVersionUID = -2847176004126232098L;

	public enum Type {
		NOTE("note"), 
		MESSAGE("message"),
		ALL(null);
		
		private String typeName;
		
		private Type(String typeName) {
			this.typeName = typeName;
		}
		
		public String getTypeName() {
			return typeName;
		}
		
	}
	
	@Inject
	private transient FacesContext fContext;
	
	private Type type = Type.ALL;

	protected WebLog(Type type) {
		this.type = type;
	}

	public void info(String message) {
		info("", message);
	}

	public void info(String title, String message) {
		fContext.addMessage(type.getTypeName(), new FacesMessage(FacesMessage.SEVERITY_INFO, title, message));
	}

	public void warn(String message) {
		warn("", message);
	}

	public void warn(String title, String message) {
		fContext.addMessage(type.getTypeName(), new FacesMessage(FacesMessage.SEVERITY_WARN, title, message));
	}

	public void error(String message) {
		error("", message);
	}

	public void error(String title, String message) {
		fContext.addMessage(type.getTypeName(), new FacesMessage(FacesMessage.SEVERITY_ERROR, title, message));
	}

	public void fatal(String message) {
		fatal("", message);
	}

	public void fatal(String title, String message) {
		fContext.addMessage(type.getTypeName(), new FacesMessage(FacesMessage.SEVERITY_FATAL, title, message));
	}
	
}
