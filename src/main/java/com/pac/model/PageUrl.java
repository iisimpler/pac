package com.pac.model;

public class PageUrl {

	private Integer id;
	
	private String name;
	
	private String status;

	public PageUrl() {
		super();
	}

	public PageUrl(String name, String status) {
		super();
		this.name = name;
		this.status = status;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
