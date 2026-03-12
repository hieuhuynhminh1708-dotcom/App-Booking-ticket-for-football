package com.example.client.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RssResponse {

    @Element(name = "channel", required = false)
    public Channel channel;
}
