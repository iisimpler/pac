package com.pac.model;


public class Match {

	private Integer id;
	
	private Integer hostId;
	
	private Integer guestId;
	
	private String rankHost;
	
	private String rankGuest;
	
	private String scoreHalf;
	
	private String scoreAll;
	
	private Integer leagueId;
	
	private Integer redHost;
	
	private Integer redGuest;
	
	private String time;
	
	private Integer round;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public Integer getGuestId() {
		return guestId;
	}

	public void setGuestId(Integer guestId) {
		this.guestId = guestId;
	}

	public String getRankHost() {
		return rankHost;
	}

	public void setRankHost(String rankHost) {
		this.rankHost = rankHost;
	}

	public String getRankGuest() {
		return rankGuest;
	}

	public void setRankGuest(String rankGuest) {
		this.rankGuest = rankGuest;
	}

	public String getScoreHalf() {
		return scoreHalf;
	}

	public void setScoreHalf(String scoreHalf) {
		this.scoreHalf = scoreHalf;
	}

	public String getScoreAll() {
		return scoreAll;
	}

	public void setScoreAll(String scoreAll) {
		this.scoreAll = scoreAll;
	}

	public Integer getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Integer leagueId) {
		this.leagueId = leagueId;
	}

	public Integer getRedHost() {
		return redHost;
	}

	public void setRedHost(Integer redHost) {
		this.redHost = redHost;
	}

	public Integer getRedGuest() {
		return redGuest;
	}

	public void setRedGuest(Integer redGuest) {
		this.redGuest = redGuest;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}
}
