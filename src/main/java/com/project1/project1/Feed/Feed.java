package com.project1.project1.Feed;

import com.project1.project1.Pricing.Market;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.User.Portfolio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class Feed {
    @Autowired
    Market market;
    @Autowired
    FeedService feedService;
    @Autowired
    Portfolio portfolio;
    //private static final Feed feedInstance = new Feed();
    private ArrayList<FeedObject> feed;

    @PostConstruct
    private void init() {
        feed = new ArrayList<>();

        addObject(new Stock("AAPL", 257.46, feedService, portfolio));
        addObject(new Stock("GOOG", 298.52, feedService, portfolio));
        addObject(new Stock("TSLA", 396.73, feedService, portfolio));
        addObject(new Stock("AMZN", 213.21, feedService, portfolio));
        addObject(new Stock("MSFT", 408.96, feedService, portfolio));
    }

//    public static Feed getFeed(){
//        return feedInstance;
//    }

    public void addObject(FeedObject object) {
        feed.add(object);
        market.registerObserver(object);
    }

    public FeedObject getObject(String name) {
        for (FeedObject object : feed) {
            if (object.getName().equals(name)) {
                return object;
            }
        }
        return null;
    }

    public ArrayList<FeedObject> getFeedObjects() {
        return feed;
    }
}
