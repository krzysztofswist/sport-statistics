package com.kswist.statistics.utils;

public enum Score {
	S03(1, 0, 3, "0:3"),
	S13(1, 1, 3, "1:3"),
	S23(1, 2, 3, "2:3"),
	S32(1, 3, 2, "3:2"),
	S31(1, 3, 1, "3:1"),
	S30(1, 3, 0, "3:0");

	private int order;
	private int userScore;
	private int opponentScore;
	private String label;

	private Score(int order, int userScore, int opponentScore, String label) {
		this.order = order;
		this.userScore = userScore;
		this.opponentScore = opponentScore;
		this.label = label;
	}

	public int getOrder() {
		return order;
	}

	public int getUserScore() {
		return userScore;
	}

	public int getOpponentScore() {
		return opponentScore;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public void setOpponentScore(int opponentScore) {
		this.opponentScore = opponentScore;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
