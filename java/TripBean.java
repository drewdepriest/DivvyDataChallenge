package com.drew.github;

import java.io.Serializable;
import java.util.Date;

public class TripBean implements Serializable
{


	public TripBean(Date startTime, int stationIdFrom, int stationIdTo,
			Float latFrom, Float longFrom, Float latTo, Float longTo,
			Boolean roundTrip, Double bearing, String direction, Float distance) {
		super();
		this.startTime = startTime;
		this.stationIdFrom = stationIdFrom;
		this.stationIdTo = stationIdTo;
		this.latFrom = latFrom;
		this.longFrom = longFrom;
		this.latTo = latTo;
		this.longTo = longTo;
		this.roundTrip = roundTrip;
		this.bearing = bearing;
		this.direction = direction;
		this.distance = distance;
	}



	/* read from Divvy_Stations_2013.csv */
	private Date startTime;			// start date/time of this trip (in milliseconds from Excel)
	private int stationIdFrom;	// Divvy ID of origin station
	private int stationIdTo;		// Divvy ID of destination station
	
	/* read from Divvy_Trips_2013.csv */
	private Float latFrom;			// latitude of origin station
	private Float longFrom;			// longitude of origin station
	private Float latTo;			// latitude of destination station
	private Float longTo;			// longitude of destination station
	
	/* calculated internally */
	private Boolean roundTrip;		// TRUE if this trip started & completed at same station
	private double bearing;			// calculated using Haversine equation
	private String direction;		// calculated using Haversine equation
	
	/* calculated and set by API */
	private Float distance;			// road-traveled distance between lat/long pairs (NOT the direct line distance)
	
	TripBean(){}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stationIdFrom;
		result = prime * result + stationIdTo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		TripBean other = (TripBean) obj;
		if (stationIdFrom != other.stationIdFrom)
			return false;
		if (stationIdTo != other.stationIdTo)
			return false;
		return true;
	}



	public Date getStartTime() {
		return startTime;
	}



	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}



	public int getStationIdFrom() {
		return stationIdFrom;
	}



	public void setStationIdFrom(int stationIdFrom) {
		this.stationIdFrom = stationIdFrom;
	}



	public int getStationIdTo() {
		return stationIdTo;
	}



	public void setStationIdTo(int stationIdTo) {
		this.stationIdTo = stationIdTo;
	}



	public Float getLatFrom() {
		return latFrom;
	}



	public void setLatFrom(Float latFrom) {
		this.latFrom = latFrom;
	}



	public Float getLongFrom() {
		return longFrom;
	}



	public void setLongFrom(Float longFrom) {
		this.longFrom = longFrom;
	}



	public Float getLatTo() {
		return latTo;
	}



	public void setLatTo(Float latTo) {
		this.latTo = latTo;
	}



	public Float getLongTo() {
		return longTo;
	}



	public void setLongTo(Float longTo) {
		this.longTo = longTo;
	}



	public Boolean getRoundTrip() {
		return roundTrip;
	}



	public void setRoundTrip(Boolean roundTrip) {
		this.roundTrip = roundTrip;
	}



	public double getBearing() {
		return bearing;
	}



	public void setBearing(double bearing) {
		this.bearing = bearing;
	}



	public String getDirection() {
		return direction;
	}



	public void setDirection(String direction) {
		this.direction = direction;
	}



	public Float getDistance() {
		return distance;
	}



	public void setDistance(Float distance) {
		this.distance = distance;
	}

	
	
}

