package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.dao.ResultDAO;
import com.kswist.statistics.db.dao.UserDAO;
import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;

@Named("resultsForUser")
@RequestScoped
public class ResultsForUser implements Serializable {
	private static final Logger logger = Logger.getLogger(ResultsForUser.class);
	private static final long serialVersionUID = 3471360120995793458L;

	@Inject
	ResultDAO resultDAO;
	
	@Inject
	UserDAO userDAO;

	@Inject
	FacesContext context;

	private void addCorrectResult(List<Result> list, Result result) {
		Result newResult = new Result();
		newResult.setConfirmed(result.isConfirmed());
		newResult.setDate(result.getDate());
		newResult.setOpponent(result.getUser());
		newResult.setOpponentPoints(result.getUserPoints());
		newResult.setResultId(result.getResultId());
		newResult.setUser(result.getOpponent());
		newResult.setUserConfirmed(result.getUserConfirmed());
		newResult.setUserPoints(result.getOpponentPoints());
		list.add(newResult);
	}

	public List<Result> getResultsByUser() {
		String login = context.getExternalContext().getRequestParameterMap()
				.get("login");
		User user = userDAO.getByLogin(login);
		logger.debug("Geting results for user: " + login);
		return getResultsByUsers(user);
	}

	private List<Result> getResultsByUsers(User user) {
		List<Result> results = new ArrayList<Result>();
		List<Result> userResults = resultDAO.getUserResults(user);
		for (Result result : userResults) {
			if (result.getUser().equals(user)) {
				results.add(result);
			}
			if (result.getOpponent().equals(user)) {
				addCorrectResult(results, result);
			}
		}
		return results;
	}

}