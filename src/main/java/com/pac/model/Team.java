package com.pac.model;

public class Team {

	private Integer id;

	private String name;
	
	private String seasonLeagueId;
	
	private String nameTra;
	
	private String nameEng;

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

	public String getSeasonLeagueId() {
		return seasonLeagueId;
	}

	public void setSeasonLeagueId(String seasonLeagueId) {
		this.seasonLeagueId = seasonLeagueId;
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
	
}
