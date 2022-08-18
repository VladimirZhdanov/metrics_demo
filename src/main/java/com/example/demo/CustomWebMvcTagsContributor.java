package com.example.demo;

import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsContributor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

// Custom Tags
//@Component
public class CustomWebMvcTagsContributor implements WebMvcTagsContributor {
    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag() {
            @Override
            public String getKey() {
                return "Key";
            }

            @Override
            public String getValue() {
                return "Value";
            }
        };

        tags.add(tag);

        return tags;
    }

    @Override
    public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
        return null;
    }
}
