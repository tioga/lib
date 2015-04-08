package org.tiogasolutions.lib.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jacobp on 3/18/2015.
 */
public class ThymeleafFactory {

  // Thymeleaf is reported by the designers to be thread
  // safe so we should be able to get away with using
  // just a single engine.
  private static TemplateEngine _templateEngine;

  public ThymeleafFactory() {
  }

  public synchronized TemplateEngine getTemplateEngine() {
    if (_templateEngine == null) {
      _templateEngine = new TemplateEngine();
      _templateEngine.setTemplateResolvers(createTemplateResolvers());
      _templateEngine.addDialect(new Java8TimeDialect());
    }
    return _templateEngine;
  }

  protected Set<ITemplateResolver> createTemplateResolvers() {
    Set<ITemplateResolver> resolvers = new HashSet<>();
    resolvers.add(createFileTemplateResolver(1));
    resolvers.add(createUrlTemplateResolver(2));
    resolvers.add(createClassPathTemplateResolver(3));
    return resolvers;
  }

  protected ClassPathTemplateResolver createClassPathTemplateResolver(int order) {
    ClassPathTemplateResolver classPathTemplateResolver = new ClassPathTemplateResolver();
    classPathTemplateResolver.setTemplateMode("HTML5");
    classPathTemplateResolver.setCacheable(false);
    classPathTemplateResolver.setOrder(order);
    return classPathTemplateResolver;
  }

  protected UrlTemplateResolver createUrlTemplateResolver(int order) {
    UrlTemplateResolver urlTemplateResolver = new UrlTemplateResolver();
    urlTemplateResolver.setTemplateMode("HTML5");
    urlTemplateResolver.setCacheable(false);
    urlTemplateResolver.setOrder(order);
    return urlTemplateResolver;
  }

  protected FileTemplateResolver createFileTemplateResolver(int order) {
    FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
    fileTemplateResolver.setTemplateMode("HTML5");
    fileTemplateResolver.setCacheable(false);
    fileTemplateResolver.setOrder(1);
    return fileTemplateResolver;
  }

  public String process(String templatePath, Context context) {
    return getTemplateEngine().process(templatePath, context);
  }

  public void process(String templatePath, Context context, Writer writer) {
    getTemplateEngine().process(templatePath, context, writer);
  }
}
