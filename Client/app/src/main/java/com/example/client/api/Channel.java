package com.example.client.api;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {

    @ElementList(entry = "item", inline = true, required = false)
    public List<RssItem> items;
}
