package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    private String url;

    @Value("${rest.template.user}")
    private String user;

    @Value("${rest.template.password}")
    private String password;

    @Bean
    RestTemplateBuilder builder(RestTemplateBuilderConfigurer configurer) {
        RestTemplateBuilder builder = configurer
                .configure(new RestTemplateBuilder())
                .basicAuthentication(user, password);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);

        return builder.uriTemplateHandler(factory);
    }
}
