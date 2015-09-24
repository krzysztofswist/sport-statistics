package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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

	public List<Map.Entry<User, Integer>> getRanking() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Map<User, Integer> rankingMap = eloDaily.get(sdf.format(new Date()));
		// Set<Map.Entry<User, Integer>> rankingSet = rankingMap.entrySet();
		// List<Map.Entry<User, Integer>> list = new ArrayList<Map.Entry<User,
		// Integer>>();
		// for(User user:users){
		// Map<>new HashMap
		// }
		// Collections.sort(list, new ValueComparator<User, Integer>());
		return null;
	}

	private void calculateRanking(List<Result> results) {
		prepareInitialElo();
		int K = 32;
		for (Result result : results) {
			String date = formatDate(result.getDate());
			handleDate(result.getDate());
			logger.debug(date);
			User user = result.getUser();
			User opponent = result.getOpponent();
			Map<String, Integer> userMap = eloDaily.get(user);
			Map<String, Integer> opponentMap = eloDaily.get(opponent);
			int userElo = userMap.get(date);
			int opponentElo = opponentMap.get(date);
			double userES = calculateExpectedScore(userElo, opponentElo);
			double opponentES = calculateExpectedScore(opponentElo, userElo);
			int userS = (result.getUserPoints() > result.getOpponentPoints())
					? 1 : 0;
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

}
