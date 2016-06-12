package com.pac.model;

import java.util.List;


public class League {

	private Integer id;
	
	private String name;
	
	private Integer countryId;
	
	private String hasSub;
	
	private String leagueType;
	
	private Integer round;
	
	private String season;
	
	private String description;
	
	private String nameTra;
	
	private String nameEng;
	
	private String nameAll;
	
	private String nameAllTra;
	
	private String nameAllEng;
	
	private List<Team> teams;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public String getLeagueType() {
		return leagueType;
	}

	public void setLeagueType(String leagueType) {
		this.leagueType = leagueType;
	}

	public String getHasSub() {
		return hasSub;
	}

	public void setHasSub(String hasSub) {
		this.hasSub = hasSub;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getRound() {
		return round;
	}
	
	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNameTra() {
		return nameTra;
	}

	public void setNameTra(String nameTra) {
		this.nameTra = nameTra;
	}

	public String getNameEng() {
		return nameEng;
	}

	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}

	public String getNameAll() {
		return nameAll;
	}

	public void setNameAll(String nameAll) {
		this.nameAll = nameAll;
	}

	public String getNameAllTra() {
		return nameAllTra;
	}

	public void setNameAllTra(String nameAllTra) {
		this.nameAllTra = nameAllTra;
	}

	public String getNameAllEng() {
		return nameAllEng;
	}

	public void setNameAllEng(String nameAllEng) {
		this.nameAllEng = nameAllEng;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

}
