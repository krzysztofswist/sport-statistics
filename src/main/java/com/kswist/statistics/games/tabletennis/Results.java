package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	private List<User> users;
	private User user;
	private Map<User, Map<String, Integer>> eloDaily;

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
		users = userDAO.getAll();
		calculateRanking(resultDAO.getAllAsc());
	}

	public List<Result> getResults() {
		return results;
	}

	public Map<User, Map<String, Integer>> getEloDaily() {
		return eloDaily;
	}

	public void setEloDaily(Map<User, Map<String, Integer>> eloDaily) {
		this.eloDaily = eloDaily;
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

	private void calculateRanking(List<Result> results) {
		prepareInitialElo();
		int K = 32;
		Calendar start = Calendar.getInstance();
		start.setTime(results.get(0).getDate());
		Calendar end = Calendar.getInstance();
		end.setTime(new Date());
		end.add(Calendar.DATE, 1);

		for (Date d = start.getTime(); start.before(end); start
				.add(Calendar.DATE, 1), d = start.getTime()) {
			handleDate(d);
			for (Result result : getResultsForDate(d)) {
				String date = formatDate(result.getDate());
				logger.debug(date);
				User user = result.getUser();
				User opponent = result.getOpponent();
				Map<String, Integer> userMap = eloDaily.get(user);
				Map<String, Integer> opponentMap = eloDaily.get(opponent);
				int userElo = userMap.get(date);
				int opponentElo = opponentMap.get(date);
				double userES = calculateExpectedScore(userElo, opponentElo);
				double opponentES = calculateExpectedScore(opponentElo,
						userElo);
				int userS = (result.getUserPoints() > result
						.getOpponentPoints()) ? 1 : 0;
				int opponentS = (result.getOpponentPoints() > result
						.getUserPoints()) ? 1 : 0;
				int userEloNew = (int) (userElo + K * (userS - userES));
				int opponentEloNew = (int) (opponentElo
						+ K * (opponentS - opponentES));
				userMap.put(date, userEloNew);
				opponentMap.put(date, opponentEloNew);
				logger.debug(user.getLogin() + " ES: " + userES);
				logger.debug(user.getLogin() + " S: " + userS);
				logger.debug(user.getLogin() + " ELO: " + userEloNew);
				logger.debug(opponent.getLogin() + " ES: " + opponentES);
				logger.debug(opponent.getLogin() + " S: " + opponentS);
				logger.debug(opponent.getLogin() + " ELO: " + opponentEloNew);
			}
		}

	}

	private void handleDate(Date date) {
		String yesterday = formatDate(getYesterday(date));
		String today = formatDate(date);
		for (Map.Entry<User, Map<String, Integer>> userMap : eloDaily
				.entrySet()) {
			if (userMap.getValue().get(today) == null) {
				userMap.getValue().put(today,
						userMap.getValue().get(yesterday));
			}
		}
	}

	private Date getYesterday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	private void prepareInitialElo() {
		eloDaily = new LinkedHashMap<User, Map<String, Integer>>();
		String date = formatDate(
				getYesterday(results.get(results.size() - 1).getDate()));

		for (User user : users) {
			Map<String, Integer> initial = new TreeMap<String, Integer>();
			initial.put(date, 1200);
			eloDaily.put(user, initial);
		}
	}

	private double calculateExpectedScore(int userElo, int opponentElo) {
		return 1.0 / (1.0 + Math.pow(10, (opponentElo - userElo) / 400.0));
	}

	private List<Result> getResultsForDate(Date date) {
		List<Result> resultsForDate = new ArrayList<Result>();
		Calendar requestedDay = Calendar.getInstance();
		requestedDay.setTime(date);
		requestedDay.set(Calendar.HOUR_OF_DAY, 0);
		requestedDay.set(Calendar.MINUTE, 0);
		requestedDay.set(Calendar.SECOND, 0);
		requestedDay.set(Calendar.MILLISECOND, 0);
		Calendar resultDay = Calendar.getInstance();

		for (Result result : results) {
			resultDay.setTime(result.getDate());
			resultDay.set(Calendar.HOUR_OF_DAY, 0);
			resultDay.set(Calendar.MINUTE, 0);
			resultDay.set(Calendar.SECOND, 0);
			resultDay.set(Calendar.MILLISECOND, 0);
			logger.debug("Requested date: " + requestedDay.getTime());
			logger.debug("Result date: " + resultDay.getTime());
			if (requestedDay.getTime().equals(resultDay.getTime())) {
				logger.debug(resultDay.getTime() + "equal to "
						+ requestedDay.getTime());
				resultsForDate.add(result);
			}
		}
		return resultsForDate;
	}

	private Map<User, List<Result>> getResultsByUsers() {
		Map<User, List<Result>> resultsMap = new HashMap<User, List<Result>>();
		for (Result result : results) {
			if (resultsMap.get(result.getUser()) == null) {
				List<Result> userResults = new ArrayList<Result>();
				userResults.add(result);
				resultsMap.put(result.getUser(), userResults);
			} else {
				resultsMap.get(result.getUser()).add(result);
			}
			if (resultsMap.get(result.getOpponent()) == null) {
				List<Result> userResults = new ArrayList<Result>();
				addCorrectResult(userResults, result);
				resultsMap.put(result.getOpponent(), userResults);
			} else {
				addCorrectResult(resultsMap.get(result.getOpponent()), result);
			}
		}
		return resultsMap;
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
	
	public List<Result> getResultsByUser(){
		String login = context.getExternalContext().getRequestParameterMap().get("login");
		User user=userDAO.getByLogin(login);
		return getResultsByUsers().get(user);
	}

}
