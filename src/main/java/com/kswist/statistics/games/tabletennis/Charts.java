package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.kswist.statistics.comparators.UserComparator;
import com.kswist.statistics.comparators.ValueComparator;
import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;
import com.kswist.statistics.security.AuthorizationBean;

@Named("charts")
@RequestScoped
public class Charts implements Serializable {
	private static final Logger logger = Logger.getLogger(Charts.class);
	private static final long serialVersionUID = 3471360120995793458L;

	LineChartModel dateModel;
	HorizontalBarChartModel horizontalBarChartModel;

	@Inject
	Ranking ranking;

	@Inject
	AuthorizationBean authBean;

	@Inject
	Results results;

	@PostConstruct
	public void init() {
		createDateModel();
		createBarModel();
		logger.debug("CreatingDateModel");
	}

	public LineChartModel getDateModel() {
		return dateModel;
	}

	public void setDateModel(LineChartModel dateModel) {
		this.dateModel = dateModel;
	}

	public BarChartModel getHorizontalBarChartModel() {
		return horizontalBarChartModel;
	}

	public void setHorizontalBarChartModel(
			HorizontalBarChartModel horizontalBarChartModel) {
		this.horizontalBarChartModel = horizontalBarChartModel;
	}

	private void createDateModel() {
		dateModel = new LineChartModel();
		Set<Map.Entry<User, Map<String, Integer>>> eloSet = ranking
				.getEloDaily().entrySet();
		List<Map.Entry<User, Map<String, Integer>>> eloList = new ArrayList<Map.Entry<User, Map<String, Integer>>>(
				eloSet);
		Collections.sort(eloList, new ValueComparator());
		for (Map.Entry<User, Map<String, Integer>> dailyElo : eloList) {

			LineChartSeries serie = new LineChartSeries();
			String date = null;
			Integer elo = null;
			for (Map.Entry<String, Integer> values : dailyElo.getValue()
					.entrySet()) {
				date = values.getKey();
				elo = values.getValue();
				logger.debug(
						dailyElo.getKey().getLogin() + ":" + date + ":" + elo);
				serie.set(date, elo);

			}
			String label = dailyElo.getKey().getFirstName() + " "
					+ dailyElo.getKey().getLastName();
			serie.setLabel(label + " (" + elo + ")");

			dateModel.addSeries(serie);
			dateModel.getAxes().put(AxisType.X, new CategoryAxis(""));
		}

		dateModel.setTitle("Ranking ELO history");
		dateModel.setLegendPosition("nw");

	}

	private void createBarModel() {
		horizontalBarChartModel = new HorizontalBarChartModel();
		Map<User, Integer> matchMap = getMatchCount();
		Set<Map.Entry<User, Integer>> matchSet = matchMap.entrySet();
		List<Map.Entry<User, Integer>> matchList = new ArrayList<Map.Entry<User, Integer>>(
				matchSet);
		Collections.sort(matchList, new UserComparator());
		for (Map.Entry<User, Integer> entry : matchList) {
			ChartSeries userSerie = new ChartSeries();
			for (Map.Entry<User, Integer> labelEntry : matchList) {
				String label = labelEntry.getKey().getFirstName() + " "
						+ labelEntry.getKey().getLastName();
				if (labelEntry.getKey().equals(entry.getKey())) {
					userSerie.set(label, entry.getValue());
				} else {
					userSerie.set(label, 0);
				}
			}
			horizontalBarChartModel.addSeries(userSerie);
		}
		Axis xAxis = horizontalBarChartModel.getAxis(AxisType.X);
		xAxis.setLabel("Matches played");
		horizontalBarChartModel.setTitle("Played Matches Count");
	}

	private Map<User, Integer> getMatchCount() {
		Map<User, Integer> matchMap = new HashMap<User, Integer>();
		for (Result result : results.getResults()) {
			User user = result.getUser();
			User opponent = result.getOpponent();
			if (matchMap.get(user) == null) {
				matchMap.put(user, 1);
			} else {
				matchMap.put(user, (matchMap.get(user)) + 1);
			}
			if (matchMap.get(opponent) == null) {
				matchMap.put(opponent, 1);
			} else {
				matchMap.put(opponent, (matchMap.get(opponent)) + 1);
			}
		}
		return matchMap;
	}

}
