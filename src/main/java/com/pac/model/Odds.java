package com.pac.model;

import java.util.Date;

public class Odds {

	private Integer id;
	
	private Integer matchId;
	
	private Integer companyId;
	
	private Float hostOdds;
	
	private Float drawOdds;
	
	private Float guestOdds;
	
	private Float hostRate;
	
	private Float drawRate;
	
	private Float guestRate;
	
	private Float returnRate;
	
	private Float hostKelly;
	
	private Float drawKelly;
	
	private Float guestKelly;
	
	private Date time;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMatchId() {
		return matchId;
	}

	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Float getHostOdds() {
		return hostOdds;
	}

	public void setHostOdds(Float hostOdds) {
		this.hostOdds = hostOdds;
	}

	public Float getDrawOdds() {
		return drawOdds;
	}

	public void setDrawOdds(Float drawOdds) {
		this.drawOdds = drawOdds;
	}

	public Float getGuestOdds() {
		return guestOdds;
	}

	public void setGuestOdds(Float guestOdds) {
		this.guestOdds = guestOdds;
	}

	public Float getHostRate() {
		return hostRate;
	}

	public void setHostRate(Float hostRate) {
		this.hostRate = hostRate;
	}

	public Float getDrawRate() {
		return drawRate;
	}

	public void setDrawRate(Float drawRate) {
		this.drawRate = drawRate;
	}

	public Float getGuestRate() {
		return guestRate;
	}

	public void setGuestRate(Float guestRate) {
		this.guestRate = guestRate;
	}

	public Float getReturnRate() {
		return returnRate;
	}

	public void setReturnRate(Float returnRate) {
		this.returnRate = returnRate;
	}

	public Float getHostKelly() {
		return hostKelly;
	}

	public void setHostKelly(Float hostKelly) {
		this.hostKelly = hostKelly;
	}

	public Float getDrawKelly() {
		return drawKelly;
	}

	public void setDrawKelly(Float drawKelly) {
		this.drawKelly = drawKelly;
	}

	public Float getGuestKelly() {
		return guestKelly;
	}

	public void setGuestKelly(Float guestKelly) {
		this.guestKelly = guestKelly;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
}
