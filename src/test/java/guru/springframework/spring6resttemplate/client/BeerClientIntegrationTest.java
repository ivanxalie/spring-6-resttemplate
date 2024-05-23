package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static guru.springframework.spring6resttemplate.client.BeerClient.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
class BeerClientIntegrationTest {

    @Autowired
    private BeerClient client;

    private static BeerDTO constructMangoBobs() {
        return BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .name("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("12345")
                .build();
    }

    @Test
    void beersWithoutName() {
        Page<BeerDTO> beers = client.beers(null, null, null, null, null);
        log(beers);
    }

    @Test
    void beers() {
        Page<BeerDTO> beers = client
                .beers(builder()
                        .name("ALE")
                        .style(BeerStyle.ALE)
                );
        log(beers);
    }

    @Test
    void getBeerById() {
        Page<BeerDTO> beers = client.beers();
        BeerDTO first = beers.getContent().getFirst();

        BeerDTO call = client.getById(first.getId());

        assertThat(call).isNotNull();
        assertThat(call).isEqualTo(first);
    }

    @Test
    void createBeer() {
        BeerDTO newBeer = constructMangoBobs();

        BeerDTO saved = client.createBeer(newBeer);
        assertThat(saved).isNotNull();
    }

    @Test
    void updateBeer() {
        BeerDTO newBeer = constructMangoBobs();

        BeerDTO saved = client.createBeer(newBeer);

        String newName = "Mango Bobs 3";
        saved.setName(newName);
        BeerDTO updated = client.updateBeer(saved);

        assertThat(newName).isEqualTo(updated.getName());
    }

    @Test
    void deleteBeer() {
        BeerDTO testToDelete = constructMangoBobs();

        BeerDTO saved = client.createBeer(testToDelete);

        assertThat(saved).isNotNull();

        client.delete(saved.getId());

        assertThrows(HttpClientErrorException.NotFound.class, () -> client.getById(testToDelete.getId()));
    }

    private void log(Page<BeerDTO> beers) {
        log.info("Beers size: {}", beers.getTotalElements());
    }
}