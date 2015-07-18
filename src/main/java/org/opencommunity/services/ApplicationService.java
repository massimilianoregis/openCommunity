package org.opencommunity.services;

import java.util.List;

import org.opencommunity.objs.Application;
import org.opencommunity.persistence.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;


@Transactional("communityTransactionManager")
@Controller
@RequestMapping("/application")
public class ApplicationService 
	{
	@Autowired
	private ApplicationRepository apps;
	
		
	static private ObjectMapper json = new ObjectMapper();

	
	
	//@Secured("Admin")		
	@RequestMapping(value="/app",method=RequestMethod.GET)
	public @ResponseBody List<Application> list() throws Exception
		{						
		return apps.findAll();		
		}
	@RequestMapping(value="/app/{id}",method=RequestMethod.GET)
	public @ResponseBody Application get(@PathVariable String id) throws Exception
		{						
		return apps.findOne(id);		
		}
	
	@RequestMapping("/name/{name}")
	public @ResponseBody Application getname(@PathVariable String name) throws Exception
		{						
		return apps.findByName(name);		
		}
	
	@RequestMapping(value="/app",method=RequestMethod.POST)
	public @ResponseBody void save(@RequestBody Application app) throws Exception
		{					
		System.out.println(app.data);
//		Application ap =app;
//		if(app.getId()!=null)	ap= apps.findOne(app.getId());
//		ap.setData(app.getData());
		apps.save(app);		
		}


	}

