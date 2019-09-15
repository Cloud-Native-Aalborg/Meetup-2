package org.mejlholm;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
class TwitterScheduler {

    @ConfigProperty(name = "CONSUMER_KEY")
    String consumerKey;

    @ConfigProperty(name = "CONSUMER_SECRET")
    String consumerSecret;

    @ConfigProperty(name = "ACCESS_TOKEN")
    String accessToken;

    @ConfigProperty(name = "ACCESS_TOKEN_SECRET")
    String accessTokenSecret;

    private Random rand = new Random();
    private ResponseList<Status> statuses;
    private ConfigurationBuilder cb;

    @PostConstruct
    private void init() {
        cb = new ConfigurationBuilder()
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
    }

    @Scheduled(every = "1h")
    void scheduleGetTweets() throws TwitterException {
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        statuses = twitter.getUserTimeline("@CodeWisdom");
    }

    String getRandomTweet() {
        if (statuses == null || statuses.isEmpty()) {
            return "No quotes yet";
        } else {
            return statuses.get(rand.nextInt(statuses.size())).getText();
        }
    }
}
