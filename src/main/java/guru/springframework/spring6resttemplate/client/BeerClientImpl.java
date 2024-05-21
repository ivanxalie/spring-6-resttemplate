package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import guru.springframework.spring6resttemplate.model.RestPageImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerClientImpl implements BeerClient {
    private static final String GET_BEER = "/api/v1/beer";

    private final RestTemplateBuilder builder;

    @Override
    public Page<BeerDTO> beers(String name,
                               BeerStyle beerStyle,
                               Boolean showInventory,
                               Integer pageNumber,
                               Integer pageSize) {
        RestTemplate template = builder.build();

        UriComponentsBuilder componentsBuilder = UriComponentsBuilder
                .fromPath(GET_BEER);

        if (StringUtils.hasText(name))
            componentsBuilder = componentsBuilder.queryParam("name", name);

        if (showInventory != null)
            componentsBuilder = componentsBuilder.queryParam("showInventory", showInventory);

        if (beerStyle != null)
            componentsBuilder = componentsBuilder.queryParam("beerStyle", beerStyle.name());

        if (pageNumber == null)
            pageNumber = 0;

        componentsBuilder.queryParam("pageNumber", pageNumber);

        if (pageSize != null)
            componentsBuilder = componentsBuilder.queryParam("pageSize", pageSize);

        ResponseEntity<RestPageImpl> response = template.getForEntity(componentsBuilder.toUriString(),
                RestPageImpl.class);

        return response.getBody();
    }
}
