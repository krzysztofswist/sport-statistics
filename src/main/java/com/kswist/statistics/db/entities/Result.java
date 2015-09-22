package com.kswist.statistics.db.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SS_RESULT")
public class Result implements Serializable {

	private static final long serialVersionUID = -6894466923770538717L;

	@Id
	@GeneratedValue(generator = "SS_RESULT_SEQ")
	@SequenceGenerator(name = "SS_RESULT_SEQ", sequenceName = "SS_RESULT_SEQ", allocationSize = 1)
	@Column(name = "EVENT_ID", insertable = false)
	private int resultId;

	@Column(name = "EVENT_DATE")
	private Date date;

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "OPPONENT_ID", nullable = false)
	private User opponent;

	@Column(name = "USER_POINTS")
	private int userPoints;

	@Column(name = "OPPONENT_POINTS")
	private int opponentPoints;

	@Column(name = "CONFIRMED")
	private boolean confirmed;

	@ManyToOne
	@JoinColumn(name = "USER_CONFIRMED_ID", nullable = false)
	private User userConfirmed;

	public boolean isConfirmed() {
		return confirmed;
	}

	public User getUserConfirmed() {
		return userConfirmed;
	}

	public void setUserConfirmed(User userConfirmed) {
		this.userConfirmed = userConfirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public int getResultId() {
		return resultId;
	}

	public User getOpponent() {
		return opponent;
	}

	public void setOpponent(User opponent) {
		this.opponent = opponent;
	}

	public int getUserPoints() {
		return userPoints;
	}

	public int getOpponentPoints() {
		return opponentPoints;
	}

	public Date getDate() {
		return date;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public void setUserPoints(int userPoints) {
		this.userPoints = userPoints;
	}

	public void setOpponentPoints(int opponentPoints) {
		this.opponentPoints = opponentPoints;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((opponent == null) ? 0 : opponent.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (opponent == null) {
			if (other.opponent != null)
				return false;
		} else if (!opponent.equals(other.opponent))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [resultId=" + resultId + ", date=" + date + ", user="
				+ user + ", opponent=" + opponent + ", userPoints=" + userPoints
				+ ", opponentPoints=" + opponentPoints + ", confirmed="
				+ confirmed + ", userConfirmed=" + userConfirmed + "]";
	}
	
	

}
