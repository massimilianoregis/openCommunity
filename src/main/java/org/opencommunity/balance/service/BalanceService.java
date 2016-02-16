package org.opencommunity.balance.service;

import java.util.List;

import org.opencommunity.balance.Balance;
import org.opencommunity.balance.persistence.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceService {
	@Autowired
	BalanceRepository balances;
	
	@RequestMapping("/balance/{user}/change")
	public @ResponseBody Balance change(@PathVariable String user, int value, String why)	{
		Balance balance = new Balance(user);
			try{balance = balances.findBalanceByUser(user).get(0);}catch(Exception e){}			
			balance.add(value, why);
			System.out.println(balance.getTotal());
		balances.save(balance);
		return balance;
	}
	@RequestMapping("/balance")
	public @ResponseBody List<Balance> getBalance(){		
		return balances.findAll();
	}
	@RequestMapping("/balance/delete")
	public @ResponseBody void delete(){		
		List<Balance> bls = balances.findAll();
		for(Balance item:bls)
			balances.delete(item.getId());
	}
	
	@RequestMapping("/balance/{user:.*}")
	public @ResponseBody Balance getBalance(@PathVariable String user){
		System.out.println(user);
		try{return balances.findBalanceByUser(user).get(0);}catch(Exception e){}
		return null;
	}
	
}
