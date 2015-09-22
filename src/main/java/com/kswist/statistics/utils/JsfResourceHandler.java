package com.kswist.statistics.utils;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ResourceWrapper;

import org.jboss.logging.Logger;

public class JsfResourceHandler extends ResourceHandlerWrapper {
	
	private static final Logger logger = Logger.getLogger(JsfResourceHandler.class);
	
	private ResourceHandler defaultHandler;
	
	public JsfResourceHandler(ResourceHandler defaultResourceHandler) {
		this.defaultHandler = defaultResourceHandler;
	}
	
	@Override
	public Resource createResource(String resourceName, String libraryName) {
		logger.tracev("Resource Name: {0}, Library Name: {1}", resourceName, libraryName);
		if ("css".equals(libraryName) || "js".equals(libraryName)) {
		  Resource r = super.createResource(resourceName, libraryName);
			return new NoCacheableResourceWrapper(r);
		} else {
			return super.createResource(resourceName, libraryName);
		}
	}

	@Override
	public ResourceHandler getWrapped() {
		return defaultHandler;
	}
	
	private static class NoCacheableResourceWrapper extends ResourceWrapper {

	  private Resource wrapped;

	  public NoCacheableResourceWrapper(Resource wrapped) {
	    this.wrapped = wrapped;
	  }
	  
	  @Override
	  public Map<String, String> getResponseHeaders() {
	    Map<String, String> headers = new HashMap<String, String>();
	    headers.putAll(super.getResponseHeaders());
	    headers.put("Cache-Control", "no-cache, no-store, must-revalidate");
	    headers.put("Pragma", "no-cache");
	    headers.put("Expires", "0");
      return headers;
	  }
	  
	  @Override
	  public Resource getWrapped() {
	    return wrapped;
	  }
	  
	  @Override
	  public String getContentType() {
	    return wrapped.getContentType();
	   }
	  
	}

}