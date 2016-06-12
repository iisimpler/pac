package com.pac.model;

import java.util.List;


public class Country {

	private Integer id;

	private String name;
	
	private String nameTra;
	
	private List<League> leagues;

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

	public String getNameTra() {
		return nameTra;
	}

	public void setNameTra(String nameTra) {
		this.nameTra = nameTra;
	}

	public List<League> getLeagues() {
		return leagues;
	}

	public void setLeagues(List<League> leagues) {
		this.leagues = leagues;
	}

}
