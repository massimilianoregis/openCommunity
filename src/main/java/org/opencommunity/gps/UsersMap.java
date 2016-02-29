package org.opencommunity.gps;

import java.util.ArrayList;
import java.util.List;

public class UsersMap {
	private List<Position> list= new ArrayList<Position>();
	
	public Position setPosition(String user, Float latitude, Float longitude){
		Position position =new Position(user, latitude,longitude);
		list.add(position);
		return position;
	}
	public List<Position> getList() {
		return list;
	}
	public void setList(List<Position> list) {
		this.list = list;
	}
	public Position get(String user){
		for(Position pos:this.list)
			if(pos.getUser().equals(user))
				return pos;
		return null;
	}
	public List<Position> around(Position me,Float meters){
		List<Position> result = new ArrayList<Position>();
		for(Position pos:this.list)
			if(pos.distance(me.getLatitude(), me.getLongitude())<meters)
				result.add(pos);
		return result;
	}
}
