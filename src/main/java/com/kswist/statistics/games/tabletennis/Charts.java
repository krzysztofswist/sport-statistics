package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.kswist.statistics.comparators.ValueComparator;
import com.kswist.statistics.db.entities.User;
import com.kswist.statistics.security.AuthorizationBean;

@Named("charts")
@RequestScoped
public class Charts implements Serializable {
	private static final Logger logger = Logger.getLogger(Charts.class);
	private static final long serialVersionUID = 3471360120995793458L;

	LineChartModel dateModel;

	@Inject
	Results results;

	@Inject
	AuthorizationBean authBean;

	@PostConstruct
	public void init() {
		createDateModel();
		logger.debug("CreatingDateModel");
	}

	public LineChartModel getDateModel() {
		return dateModel;
	}

	public void setDateModel(LineChartModel dateModel) {
		this.dateModel = dateModel;
	}

	private void createDateModel() {
		dateModel = new LineChartModel();
		Set<Map.Entry<User, Map<String, Integer>>> eloSet = results
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
			serie.setLabel(dailyElo.getKey().getFirstName() + " "
					+ dailyElo.getKey().getLastName() + " (" + elo + ")");
			dateModel.addSeries(serie);
			dateModel.getAxes().put(AxisType.X, new CategoryAxis(""));
		}

		dateModel.setTitle("Ranking ELO history");
		dateModel.setLegendPosition("nw");

	}

}
