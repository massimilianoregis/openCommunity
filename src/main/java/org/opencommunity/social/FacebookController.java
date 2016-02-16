package org.opencommunity.social;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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