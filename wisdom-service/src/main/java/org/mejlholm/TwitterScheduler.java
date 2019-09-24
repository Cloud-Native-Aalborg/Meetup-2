package org.mejlholm;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.opentracing.Traced;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
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
    private List<Status> statuses = new ArrayList<>();
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

    @Scheduled(every = "20m")
    void scheduleGetTweets() throws TwitterException {
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        statuses = twitter.getUserTimeline("@CodeWisdom").stream().filter(s -> !s.isRetweet()).collect(Collectors.toList());
    }

    Tweet getRandomTweet() {
        if (!statuses.isEmpty()) {
            Status status = statuses.get(rand.nextInt(statuses.size()));
            String rawText = status.getText();

            Pattern p = Pattern.compile("^(.*)-(.*)$");
            Matcher m = p.matcher(rawText);
            if (m.find()) {
                return new Tweet(m.group(1), m.group(2));
            } else {
                log.info("Unable to parse: " + rawText);
                return new Tweet("Sorry - the quote was un-digestible!", "Arne Mejlholm");
            }
        } else {
            return new Tweet("Glimpse in the matrix, carry on...", "The system");
        }
    }

    @Gauge(name = "numberOfTweets", unit = MetricUnits.NONE, description = "Shows the number of tweets.")
    public int getNumberOfTweets() {
        return statuses.size();
    }
}
