package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
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

	List<Result> resultsByUser;

	@Inject
	ResultDAO resultDAO;

	@Inject
	UserDAO userDAO;

	@Inject
	FacesContext context;

	List<Result> results;

	@PostConstruct
	private void init() {
		resultsByUser = generateResultsByUser();
	}

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

	public List<Result> generateResultsByUser() {
		String login = context.getExternalContext().getRequestParameterMap()
				.get("login");
		User user = userDAO.getByLogin(login);
		logger.debug("Geting results for user: " + login);
		return getResultsByUsers(user);
	}

	public List<Result> getResultsByUser() {
		return resultsByUser;
	}

	public void setResultsByUser(List<Result> resultsByUser) {
		this.resultsByUser = resultsByUser;
	}

	private List<Result> getResultsByUsers(User user) {
		results = new ArrayList<Result>();
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

	public List<Map.Entry<User, Integer>> getMatchPerUser() {
		Map<User, Integer> map = new HashMap<User, Integer>();
		for (Result result : results) {
			User opponent = result.getOpponent();
			if (map.get(opponent) == null) {
				map.put(opponent, 1);
			} else {
				map.put(opponent, map.get(opponent) + 1);
			}
		}
		Set<Map.Entry<User, Integer>> set = map.entrySet();
		return new ArrayList<Map.Entry<User, Integer>>(set);
	}

}
