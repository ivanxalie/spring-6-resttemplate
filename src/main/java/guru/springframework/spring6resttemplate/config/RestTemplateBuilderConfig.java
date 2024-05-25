package guru.springframework.spring6resttemplate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@RequiredArgsConstructor
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    private String url;

    @Bean
    OAuth2AuthorizedClientManager manager(
            ClientRegistrationRepository repository, OAuth2AuthorizedClientService service) {

        var authProvider = OAuth2AuthorizedClientProviderBuilder
                .builder()
                .clientCredentials()
                .build();
        var authClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(repository, service);
        authClientManager.setAuthorizedClientProvider(authProvider);

        return authClientManager;
    }

    @Bean
    RestTemplateBuilder builder(RestTemplateBuilderConfigurer configurer,
                                OAuthClientInterceptor interceptor) {
        return configurer
                .configure(new RestTemplateBuilder())
                .additionalInterceptors(interceptor)
                .uriTemplateHandler(new DefaultUriBuilderFactory(url));
    }
}