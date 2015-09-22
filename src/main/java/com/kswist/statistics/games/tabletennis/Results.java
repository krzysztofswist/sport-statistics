package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.kswist.statistics.comparators.ValueComparator;
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

	@PostConstruct
	public void init() {
		user = userDAO.getByLogin(authBean.getUser());
		results = resultDAO.getAll();
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

	public List<Map.Entry<User, Integer>> getRanking() {
		Map<User, Integer> rankingMap = calculateRanking(results);
		Set<Map.Entry<User, Integer>> rankingSet = rankingMap.entrySet();
		List<Map.Entry<User, Integer>> list= new ArrayList<Map.Entry<User, Integer>>(rankingSet);
		Collections.sort(list, new ValueComparator<User, Integer>());
		return list;
	}

	private Map<User, Integer> calculateRanking(List<Result> results) {
		int K = 32;
		Integer initialRanking = 1200;
		Map<User, Integer> eloMap = new HashMap<User, Integer>();
		for (Result result : results) {
			if (eloMap.get(result.getUser()) == null)
				eloMap.put(result.getUser(), initialRanking);
			if (eloMap.get(result.getOpponent()) == null)
				eloMap.put(result.getOpponent(), initialRanking);
			User user = result.getUser();
			User opponent = result.getOpponent();
			int userElo = eloMap.get(user);
			int opponentElo = eloMap.get(opponent);
			double userES = calculateExpectedScore(userElo, opponentElo);
			logger.debug(user.getLogin() + " ES: " + userES);
			double opponentES = calculateExpectedScore(opponentElo, userElo);
			logger.debug(opponent.getLogin() + " ES: " + opponentES);

			int userEloNew = (int) (userElo
					+ K * ((result.getUserPoints() > result.getOpponentPoints()
							? 1 : 0) - userES));
			logger.debug(user.getLogin() + " ELO: " + userEloNew);
			int opponentEloNew = (int) (opponentElo
					+ K * ((result.getOpponentPoints() > result.getUserPoints()
							? 1 : 0) - opponentES));
			logger.debug(opponent.getLogin() + " ELO: " + opponentEloNew);
			eloMap.put(user, userEloNew);
			eloMap.put(opponent, opponentEloNew);
		}
		return eloMap;

	}

	private double calculateExpectedScore(int userElo, int opponentElo) {
		return 1.0 / (1.0 + Math.pow(10, (opponentElo - userElo) / 400.0));
	}

}
