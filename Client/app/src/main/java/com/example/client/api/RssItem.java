package com.example.client.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class RssItem {

    @Element(name = "title", required = false)
    public String title;

    @Element(name = "link", required = false)
    public String link;

    @Element(name = "description", required = false)
    public String description;

    @Element(name = "encoded", required = false)
    @Namespace(prefix = "content", reference = "http://purl.org/rss/1.0/modules/content/")
    public String content;
}
