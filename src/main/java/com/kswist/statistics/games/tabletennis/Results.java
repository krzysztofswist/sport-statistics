package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.dao.ResultDAO;
import com.kswist.statistics.db.dao.UserDAO;
import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;
import com.kswist.statistics.security.AuthorizationBean;
import com.kswist.statistics.utils.WebMessage;

@Named("results")
@SessionScoped
public class Results implements Serializable {
	private static final Logger logger = Logger.getLogger(Results.class);
	private static final long serialVersionUID = 3471360120995793458L;

	private List<Result> results;
	private User user;

	@Inject
	WebMessage message;

	@Inject
	AuthorizationBean authBean;

	@Inject
	UserDAO userDAO;

	@Inject
	ResultDAO resultDAO;

	@Inject
	FacesContext context;

	@PostConstruct
	public void init() {
		user = userDAO.getByLogin(authBean.getUser());
		results = resultDAO.getAllDesc();
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public void confirm(Result result) {
		try {
			result.setConfirmed(true);
			resultDAO.update(result);
			message.info("Result confirmed", "message");
		} catch (Exception e) {
			String msg = "Can't update result: " + result;
			message.error(msg, "message");
			logger.error(msg);
		}
	}

	public void delete(Result result) {
		try {
			resultDAO.delete(result);
			message.info("Result deleted", "message");
		} catch (Exception e) {
			String msg = "Can't remove result: " + result;
			message.error(msg, "message");
			logger.error(msg);
		}
	}

	public boolean renderConfirmed(Result result) {
		if (!result.isConfirmed() && result.getUserConfirmed().equals(user))
			return true;
		return false;
	}

}
