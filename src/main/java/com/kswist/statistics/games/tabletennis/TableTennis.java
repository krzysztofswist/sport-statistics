package com.kswist.statistics.games.tabletennis;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.kswist.statistics.db.dao.ResultDAO;
import com.kswist.statistics.db.dao.UserDAO;
import com.kswist.statistics.db.entities.Result;
import com.kswist.statistics.db.entities.User;
import com.kswist.statistics.security.AuthorizationBean;
import com.kswist.statistics.utils.MailSender;
import com.kswist.statistics.utils.Score;
import com.kswist.statistics.utils.WebMessage;

@Named("tableTennis")
@RequestScoped
public class TableTennis implements Serializable {
	private static final Logger logger = Logger.getLogger(TableTennis.class);
	private static final long serialVersionUID = 3471360120995793458L;

	private List<User> users;
	private User user;
	private User opponent;
	private Score score;

	@Inject
	WebMessage message;

	@Inject
	AuthorizationBean authBean;

	@Inject
	MailSender mailSender;

	@Inject
	UserDAO userDAO;

	@Inject
	ResultDAO resultDAO;

	@PostConstruct
	public void init() {
		users = userDAO.getAll();
		user = userDAO.getByLogin(authBean.getUser());
		users.remove(user);
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getOpponent() {
		return opponent;
	}

	public void setOpponent(User opponent) {
		this.opponent = opponent;
	}

	public void saveResult() {
		try {
			Result result = new Result();
			result.setUser(user);
			result.setOpponent(opponent);
			result.setUserPoints(score.getUserScore());
			result.setOpponentPoints(score.getOpponentScore());
			result.setDate(new Date());
			result.setUserConfirmed(opponent);
			result.setConfirmed(false);

			resultDAO.save(result);
			String subject = "There is a result waiting for you to confirm from User: "
					+ user.getFirstName() + " " + user.getLastName();
			String body = "Dear Mr/Ms " + opponent.getFirstName() + " "
					+ opponent.getLastName() + ",</BR></BR>"
					+ "There is a result waiting for you to confirm from user: "
					+ user.getFirstName() + " " + user.getLastName() + "</BR>"
					+ "You can confirm or delete it at "
					+ "http://plbydn0h334352.pl.alcatel-lucent.com:8081/sport-statistics/ </BR></BR> Best regards,</BR> Sport statistics mail deamon ;-)";
			mailSender.send(user.getEmail(), opponent.getEmail(), subject, body);
			String msg = "Result saved!";
			message.info(msg, "message");
			logger.debug(msg);
		} catch (Exception e) {
			String msg = "Can't save result!";
			message.error(msg, "message");
			logger.debug(msg);
		}
	}

	public Score[] getScores() {
		return Score.values();
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
