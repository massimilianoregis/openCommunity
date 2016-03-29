package org.opencommunity.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/community/facebook")
public class FacebookController {
	
    private Facebook facebook;
    public FacebookController() {}
    @Autowired
    public FacebookController(Facebook facebook) {
        this.facebook = facebook;
        
    }
    @RequestMapping("/publish")
    public @ResponseBody String post(String msg,String img) throws Exception
    	{    	
    	
    	return facebook.pageOperations().postPhoto("603116799778109",msg,new UrlResource(img));    	
    	}    
    @RequestMapping("/pages")
    public @ResponseBody PagedList<Account> pages()
    	{    	
    	return facebook.pageOperations().getAccounts();
    	}
    @RequestMapping("/page/{id}")
    public @ResponseBody Page page(@PathVariable String id)
    	{    		
    	return facebook.pageOperations().getPage(id);
    	}
    @RequestMapping("/page/{id}/picture")
    public @ResponseBody Page pagePicture(@PathVariable String id)
    	{    		
    	return null;
    	}
    @RequestMapping("/friends")
    public @ResponseBody PagedList<Post> friends()
    	{    	
    	//return facebook.pageOperations().post(arg0, arg1)
    	return facebook.feedOperations().getHomeFeed();
    	}
    @RequestMapping("/")
    public String helloFacebook(Model model) {
        if (!facebook.isAuthorized()) {
            return "redirect:/connect/facebook";
        }

        model.addAttribute(facebook.userOperations().getUserProfile());
        PagedList<Post> homeFeed = facebook.feedOperations().getHomeFeed();
       
        model.addAttribute("feed", homeFeed);

        //return "hello";
        return "redirect:/connect/hello";
    }

}