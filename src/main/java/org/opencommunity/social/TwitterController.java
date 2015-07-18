package org.opencommunity.social;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@Controller
@RequestMapping("/community/twitter")
public class TwitterController {

    private Twitter twitter;

    public TwitterController() {}
    @Autowired
    public TwitterController(Twitter twitter) {
        this.twitter = twitter;        
    }

    @RequestMapping(method=RequestMethod.GET)
    public String helloTwitter(Model model) {
        if (!twitter.isAuthorized()) {
            return "redirect:/connect/twitter";
        }

        model.addAttribute(twitter.userOperations().getUserProfile());
        CursoredList<TwitterProfile> friends = twitter.friendOperations().getFriends();
        model.addAttribute("friends", friends);
        return "helloTwitter";
    }

}