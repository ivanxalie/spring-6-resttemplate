package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder builder;

    @Override
    public Page<BeerDTO> beers(String name,
                               BeerStyle beerStyle,
                               Boolean showInventory,
                               Integer pageNumber,
                               Integer pageSize) {
        RestTemplate template = builder.build();

        UriComponentsBuilder componentsBuilder = UriComponentsBuilder
                .fromPath(BEER_PATH);

        if (StringUtils.hasText(name))
            componentsBuilder = componentsBuilder.queryParam("name", name);

        if (showInventory != null)
            componentsBuilder = componentsBuilder.queryParam("showInventory", showInventory);

        if (beerStyle != null)
            componentsBuilder = componentsBuilder.queryParam("beerStyle", beerStyle.name());

        if (pageNumber != null)
            componentsBuilder.queryParam("pageNumber", pageNumber);

        if (pageSize != null)
            componentsBuilder = componentsBuilder.queryParam("pageSize", pageSize);

        ResponseEntity<BeerDTOPageImpl> response = template.getForEntity(componentsBuilder.toUriString(),
                BeerDTOPageImpl.class);

        return response.getBody();
    }

    @Override
    public BeerDTO getById(UUID id) {
        RestTemplate template = builder.build();
        return template.getForObject(BEER_BY_ID, BeerDTO.class, id);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        RestTemplate template = builder.build();
        URI uri = template.postForLocation(BEER_PATH, beerDTO);
        return template.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO dto) {
        RestTemplate template = builder.build();
        template.put(BEER_BY_ID, dto, dto.getId());
        return getById(dto.getId());
    }

    @Override
    public void delete(UUID id) {
        RestTemplate template = builder.build();
        template.delete(BEER_BY_ID, id);
    }
}