package no.westerdals.quiz;

import io.swagger.jaxrs.config.BeanConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    private final Set<Class<?>> classes;

    public ApplicationConfig() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.0.1");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/quiz/api");
        beanConfig.setResourcePackage("no.westerdals.quiz");

        beanConfig.setScan(true);

        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(CategoryRestImpl.class);
        classes.add(QuestionRestImpl.class);
        classes.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        classes.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        this.classes = Collections.unmodifiableSet(classes);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
