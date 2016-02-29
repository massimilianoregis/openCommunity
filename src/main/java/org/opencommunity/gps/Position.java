package org.opencommunity.gps;

public class Position {
	private String user;
	private Float latitude;
	private Float longitude;
	private Long time;
	public Position() {
		// TODO Auto-generated constructor stub
	}
	public Position(String user,Float latitude,Float longitude) {
		this.user=user;
		this.latitude=latitude;
		this.longitude=longitude;
	}
	public Float distance(Float latitude, Float longitude)
		{
		float lat1 = latitude;
		float lat2 = this.latitude;
		float lng1 = longitude;
		float lng2 = this.longitude;
		double earthRadius = 6371000; //meters
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    float dist = (float) (earthRadius * c);
	    
	    return dist;
		}
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	public String toString() {
	
		return user+" ("+latitude+","+longitude+")";
	}
	
	public static void main(String[] args) {
		Position pos = new Position("max",45.358751F, 9.157463F);
		System.out.println(pos.distance(45.428320F, 9.079664F));
	}
}
