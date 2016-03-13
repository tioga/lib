// Copyright (c) 2010-2014, Munchie Monster, LLC.

package org.tiogasolutions.lib.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TiogaPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final Logger log = LoggerFactory.getLogger(TiogaPropertyPlaceholderConfigurer.class);

    public TiogaPropertyPlaceholderConfigurer(String propertyName, String locationString) throws FileNotFoundException, MalformedURLException {
        if (StringUtils.isBlank(propertyName)) {
            String msg = "Unable to locate the secret properties file - the system or environment property name was not specified.";
            throw new IllegalArgumentException(msg);
        }

        List<Resource> resources = new ArrayList<>();

        List<String> locations = new ArrayList<>();
        Collections.addAll(locations, locationString.split(","));

        String secretLocation = EnvUtils.findProperty(propertyName, "");
        if (StringUtils.isBlank(secretLocation)) {
            log.warn("Unable to locate the secret properties file - the system or environment property \"{}\" was found but is null.", propertyName);
        }
        locations.add(secretLocation);

        for (String location : locations) {
            if (location == null) {
                // The secret location is null, for example
                // the System property was explicitly set to null
                continue;
            }

            location = location.trim();

            if (StringUtils.isBlank(location)) {
                continue; // Just skip it...

            } if (location.startsWith("file:")) {
                File file = new File(URI.create(location));
                resources.add(new FileSystemResource(file));

            } else if (location.startsWith("classpath:")) {
                location = location.substring(10);
                resources.add(new ClassPathResource(location));

            } else if (location.startsWith("http:") || location.startsWith("https:")) {
                resources.add(new UrlResource(location));

            } else {
                String msg = String.format("Cannot create resource from \"%s\".", location);
                throw new IllegalArgumentException(msg);
            }
        }

        setLocations(ReflectUtils.toArray(Resource.class, resources));
    }
}
