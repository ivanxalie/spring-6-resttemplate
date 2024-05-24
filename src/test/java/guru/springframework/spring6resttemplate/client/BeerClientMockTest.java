package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.client.BeerClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {
    static final String URL = "http://localhost:8080";
    private static final String TOKEN = "Basic dXNlcjpwYXNzd29yZA==";
    MockRestServiceServer server;
    @Autowired
    RestTemplateBuilder builder;

    @Autowired
    ObjectMapper mapper;

    BeerClient beerClient;
    BeerDTO dto;
    String response;

    @Mock
    RestTemplateBuilder mockBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate template = builder.build();

        server = MockRestServiceServer.bindTo(template).build();

        when(mockBuilder.build()).thenReturn(template);

        beerClient = new BeerClientImpl(mockBuilder);

        dto = createBeerDto();
        response = mapper.writeValueAsString(dto);
    }

    @Test
    void beers() throws JsonProcessingException {
        String response = mapper.writeValueAsString(createPage());
        server
                .expect(method(GET))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestTo(URL + BEER_PATH))
                .andRespond(withSuccess(response, APPLICATION_JSON));


        Page<BeerDTO> beers = beerClient.beers();
        assertThat(beers.getTotalElements()).isGreaterThan(0);

        server.verify();
    }

    private BeerDTOPageImpl createPage() {
        return new BeerDTOPageImpl(Collections.singletonList(dto),
                new BeerDTOPageImpl.PageDetails(1, 1, 1)
        );
    }

    BeerDTO createBeerDto() {
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .name("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("12345")
                .build();
    }

    @Test
    void beersWithQueryParam() throws JsonProcessingException {
        String response = mapper.writeValueAsString(createPage());

        URI uri = UriComponentsBuilder
                .fromHttpUrl(URL + BEER_PATH)
                .queryParam("name", "ALE")
                .build().toUri();

        server
                .expect(method(GET))
                .andExpect(requestTo(uri))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(queryParam("name", "ALE"))
                .andRespond(withSuccess(response, APPLICATION_JSON));

        Page<BeerDTO> beers = beerClient.beers(builder().name("ALE"));
        assertThat(beers).isNotNull();
        assertThat(beers.getTotalElements()).isGreaterThan(0);

        server.verify();
    }

    @Test
    void getById() {
        mockGetOperation();

        BeerDTO responseDto = beerClient.getById(dto.getId());
        assertThat(responseDto).isNotNull().isEqualTo(dto);

        server.verify();
    }

    private void mockGetOperation() {
        server
                .expect(method(GET))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestToUriTemplate(URL + BEER_BY_ID, dto.getId()))
                .andRespond(withSuccess(response, APPLICATION_JSON));
    }

    @Test
    void createBeer() {
        URI uri = UriComponentsBuilder.fromPath(BEER_BY_ID).build(dto.getId());

        server
                .expect(method(POST))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestTo(URL + BEER_PATH))
                .andRespond(withCreatedEntity(uri));

        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(dto);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(dto.getId());

        server.verify();
    }

    @Test
    void updateBeer() {
        server
                .expect(method(PUT))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestToUriTemplate(URL + BEER_BY_ID, dto.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO updatedBeer = beerClient.updateBeer(dto);
        assertThat(updatedBeer).isNotNull();
        assertThat(updatedBeer.getId()).isEqualTo(dto.getId());

        server.verify();
    }

    @Test
    void delete() {
        server
                .expect(method(DELETE))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestToUriTemplate(URL + BEER_BY_ID, dto.getId()))
                .andRespond(withNoContent());

        beerClient.delete(dto.getId());
        server.verify();
    }

    @Test
    void delete404() {
        server
                .expect(method(DELETE))
                .andExpect(header("Authorization", TOKEN))
                .andExpect(requestToUriTemplate(URL + BEER_BY_ID, dto.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.NotFound.class, () -> beerClient.delete(dto.getId()));

        server.verify();
    }
}