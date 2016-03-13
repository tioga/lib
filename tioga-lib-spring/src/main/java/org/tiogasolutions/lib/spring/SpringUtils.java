package org.tiogasolutions.lib.spring;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public abstract class SpringUtils {

    public static AbstractXmlApplicationContext createXmlConfigApplicationContext(String xmlConfigPath, String...activeProfiles) {

        boolean classPath = xmlConfigPath.startsWith("classpath:");
        AbstractXmlApplicationContext applicationContext = classPath ?
            new ClassPathXmlApplicationContext() :
            new FileSystemXmlApplicationContext();

        applicationContext.setConfigLocation(xmlConfigPath);
        applicationContext.getEnvironment().setActiveProfiles(activeProfiles);
        applicationContext.refresh();
        return applicationContext;
    }
}
