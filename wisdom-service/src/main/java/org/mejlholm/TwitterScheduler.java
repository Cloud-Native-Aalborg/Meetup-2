package org.mejlholm;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
@Traced
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
                .setIncludeMyRetweetEnabled(false)
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
