package org.opencommunity.gps;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gps")
public class GpsController {
	private UsersMap map = new UsersMap();
	
	//@MessageMapping("/hello")
	//@SendTo("/topic/greetings")
	public UsersMap sendMyData(Map mp){
		map.setPosition((String)mp.get("name"), new Float( (Integer)mp.get("latitude")), new Float((Integer)mp.get("longitude")));
		System.out.println(map.getList());
		return map;
	}
	
	
	@RequestMapping("/{user}/aroundme")
	public List<Position> aroundme(@PathVariable String user)	{
		Position me = map.get(user);
		return map.around(me, 20000F);
	}
	@RequestMapping("/gps/{user}/{latitude:.+}/{longitude:.+}")
	public @ResponseBody void register(@PathVariable String user,@PathVariable Float latitude,@PathVariable Float longitude)	{		
		Position me = map.get(user);		
		if(me==null) me=map.setPosition(user, latitude, longitude);
			me.setLatitude(latitude);
			me.setLongitude(longitude);
			me.setTime(System.currentTimeMillis());
			
		synchronized (me) {
			me.notifyAll();
		}
	}
	@RequestMapping("/find/{user}")
	public @ResponseBody Position sendMyData(@PathVariable String user){
		Position pos = map.get(user);
		synchronized (pos) {
			try{pos.wait();}catch(Exception e){}
			}	
		return pos;
	}
}
