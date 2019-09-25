package org.mejlholm;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.opentracing.Traced;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
@Traced
@RegisterForReflection
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
    private List<Tweet> tweets = new ArrayList<>();

    @Scheduled(every = "1h")
    void scheduleGetTweets() throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .setIncludeMyRetweetEnabled(false)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        tweets = twitter.getUserTimeline("@CodeWisdom").stream()
                .filter(s -> !s.isRetweet())
                .map(s -> parseText(s.getText()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    Tweet getRandomTweet() {
        if (tweets.isEmpty()) {
            return new Tweet("Glimpse in the matrix...", "Trinity");
        } else {
            return tweets.get(rand.nextInt(tweets.size()));
        }
    }

    private Tweet parseText(String text) {
        Pattern p = Pattern.compile("^(.*)-(.*)$");
        Matcher m = p.matcher(text);

        if (m.find()) {
            return new Tweet(m.group(1), m.group(2));
        }
        return null;
    }

    @Gauge(name = "numberOfTweets", unit = MetricUnits.NONE, description = "Shows the number of tweets.")
    public int getNumberOfTweets() {
        return tweets.size();
    }
}
