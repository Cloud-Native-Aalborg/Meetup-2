package org.mejlholm;

import lombok.Getter;

@Getter
class Tweet {

    private String author;
    private String quote;

    Tweet(String quote, String author) {
        this.author = author;
        this.quote = quote;
    }
}
