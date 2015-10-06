package com.kswist.statistics.games.tabletennis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.junit.Test;

import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;

import junit.framework.TestCase;

public class RankingTests extends TestCase {
	private static final Logger logger = Logger.getLogger(RankingTests.class);

	@Test
	public void testCalculateScore() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Ranking ranking = new Ranking();
		Method methodCalculateScore = Ranking.class
				.getDeclaredMethod("calculateScore", int.class, int.class);
		methodCalculateScore.setAccessible(true);

		int lost, scored;
		double result;
		scored = 3;
		lost = 0;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 < result && result <= 1.0);
		scored = 3;
		lost = 1;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 < result && result < 1.0);
		scored = 3;
		lost = 2;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 < result && result < 1.0);
		scored = 2;
		lost = 3;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 < result && result < 1.0);
		scored = 1;
		lost = 3;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 < result && result < 1.0);
		scored = 0;
		lost = 3;
		result = (Double) methodCalculateScore.invoke(ranking, scored, lost);
		logger.info("Result for " + scored + ":" + lost + " is: " + result);
		assertTrue(0.0 <= result && result < 1.0);

	}

	@Test
	public void testCalculateNewElo() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Ranking ranking = new Ranking();
		Method methodCalculateNewElo = Ranking.class.getDeclaredMethod(
				"calculateNewElo", int.class, int.class, double.class,
				double.class);
		methodCalculateNewElo.setAccessible(true);

		int elo, K = 32;
		Integer newElo;
		double S, ES;
		S = 1.0;
		ES = 0.7;
		elo = 1200;
		// return (int) (elo + K * (S - ES));
		newElo = (Integer) methodCalculateNewElo.invoke(ranking, elo, K, S, ES);
		logger.info("Elo diff for S: " + S + ", ES: " + ES + " is: "
				+ (newElo - elo));

	}

	@Test
	public void testCalculateNewEloForResult() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Ranking ranking = new Ranking();
		Method methodCalculateNewElo = Ranking.class
				.getDeclaredMethod("calculateNewElo", Result.class);
		methodCalculateNewElo.setAccessible(true);

		Method methodHandleDate = Ranking.class.getDeclaredMethod("handleDate",
				Date.class);
		methodHandleDate.setAccessible(true);

		List<Data> datas = new ArrayList<Data>(30);
		Data data;
		data = new Data(1200, 1200, 3, 0);
		datas.add(data);
		data = new Data(1200, 1200, 3, 1);
		datas.add(data);
		data = new Data(1200, 1200, 3, 2);
		datas.add(data);
		data = new Data(1200, 1200, 2, 3);
		datas.add(data);
		data = new Data(1200, 1200, 1, 3);
		datas.add(data);
		data = new Data(1200, 1200, 0, 3);
		datas.add(data);
		
		data = new Data(1300, 1000, 3, 0);
		datas.add(data);
		data = new Data(1300, 1000, 3, 1);
		datas.add(data);
		data = new Data(1300, 1000, 3, 2);
		datas.add(data);
		data = new Data(1300, 1000, 2, 3);
		datas.add(data);
		data = new Data(1300, 1000, 1, 3);
		datas.add(data);
		data = new Data(1300, 1000, 0, 3);
		datas.add(data);

		data = new Data(1500, 1000, 3, 0);
		datas.add(data);
		data = new Data(1500, 1000, 3, 1);
		datas.add(data);
		data = new Data(1500, 1000, 3, 2);
		datas.add(data);
		data = new Data(1500, 1000, 2, 3);
		datas.add(data);
		data = new Data(1500, 1000, 1, 3);
		datas.add(data);
		data = new Data(1500, 1000, 0, 3);
		datas.add(data);
		
		data = new Data(1304, 1087, 3, 0);
		datas.add(data);
		data = new Data(1311, 1079, 3, 0);
		datas.add(data);
		data = new Data(1317, 1072, 3, 0);
		datas.add(data);
		
		for (Data d : datas) {

			Map<User, Map<String, Integer>> eloDaily;
			Map<String, Integer> elo;
			Map<String, Integer> elo2;
			Result result = new Result();

			User user = new User();
			user.setLogin("user");

			User opponent = new User();
			opponent.setLogin("opponent");

			Calendar calendar = Calendar.getInstance();
			calendar.set(2015, 0, 2);
			Date today = calendar.getTime();
			calendar.set(2015, 0, 1);
			Date yesterday = calendar.getTime();

			result.setUser(user);
			result.setOpponent(opponent);
			result.setUserPoints(d.getUserPoints());
			result.setOpponentPoints(d.getOpponentPoints());
			result.setDate(today);

			eloDaily = new LinkedHashMap<User, Map<String, Integer>>();

			elo = new HashMap<String, Integer>();
			elo.put(formatDate(yesterday), d.getElo());
			eloDaily.put(user, elo);

			elo2 = new HashMap<String, Integer>();
			elo2.put(formatDate(yesterday), d.getElo2());
			eloDaily.put(opponent, elo2);

			ranking.setEloDaily(eloDaily);
			methodHandleDate.invoke(ranking, today);
			methodCalculateNewElo.invoke(ranking, result);
			logger.info(
					"Result " + d.userPoints + ":" + d.opponentPoints
							+ " |" + user.getLogin() + " from: "
							+ ranking.getEloDaily().get(user)
									.get(formatDate(yesterday))
							+ " to: "
							+ ranking.getEloDaily().get(user)
									.get(formatDate(today))
							+ " |" + opponent.getLogin() + " from: "
							+ ranking.getEloDaily().get(opponent)
									.get(formatDate(yesterday))
							+ " to: " + ranking.getEloDaily().get(opponent)
									.get(formatDate(today)));
		}
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	private class Data {
		int elo;
		int elo2;
		int userPoints;
		int opponentPoints;

		public Data(int elo, int elo2, int userPoints, int opponentPoints) {
			super();
			this.elo = elo;
			this.elo2 = elo2;
			this.userPoints = userPoints;
			this.opponentPoints = opponentPoints;
		}

		public int getElo() {
			return elo;
		}

		public int getElo2() {
			return elo2;
		}

		public int getUserPoints() {
			return userPoints;
		}

		public int getOpponentPoints() {
			return opponentPoints;
		}

	}
}
