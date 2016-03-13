// Copyright (c) 2010-2014, Munchie Monster, LLC.

package org.tiogasolutions.lib.spring;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.ReflectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TiogaPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public TiogaPropertyPlaceholderConfigurer(String propertyName, String locationString) throws FileNotFoundException, MalformedURLException {

        List<Resource> resources = new ArrayList<>();

        List<String> locations = new ArrayList<>();
        Collections.addAll(locations, locationString.split(","));
        locations.add(EnvUtils.findProperty(propertyName, ""));

        for (String location : locations) {
            location = location.trim();

            if (location.startsWith("file:")) {
                File file = new File(URI.create(location));
                resources.add(new FileSystemResource(file));

            } else if (location.startsWith("classpath:")) {
                location = location.substring(10);
                resources.add(new ClassPathResource(location));

            } else if (location.startsWith("http:") || location.startsWith("https:")) {
                resources.add(new UrlResource(location));

            } else {
                String msg = "Cannot create resource from " + location;
                throw new IllegalArgumentException(msg);
            }
        }

        setLocations(ReflectUtils.toArray(Resource.class, resources));
    }
}
