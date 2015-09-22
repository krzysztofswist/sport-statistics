package com.kswist.statistics.converters;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.dao.UserDAO;
import com.kswist.statistics.db.entities.User;

@Named("userConverter")
@RequestScoped
public class UserConverter implements Serializable, Converter {

	private static final long serialVersionUID = 9090301663535289960L;

	private static final Logger logger = Logger.getLogger(UserConverter.class);

	@Inject
	private transient UserDAO userDAO;

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component,
			String value) {
		logger.debug("getAsObject: " + value);
		if (value == null) {
			logger.debug("Returning null");
			return null;
		}
		User user = userDAO.findForConverter(value);
		logger.debug("User: " + user + " for value:" + value);
		return user;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object o) {
		logger.debug("Object: " + o);
		return ((User) o).getFirstName() + " " + ((User) o).getLastName();
	}
}
