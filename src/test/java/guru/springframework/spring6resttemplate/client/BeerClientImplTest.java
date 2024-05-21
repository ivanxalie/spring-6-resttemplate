package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static guru.springframework.spring6resttemplate.client.BeerClient.builder;

@SpringBootTest
@Slf4j
class BeerClientImplTest {

    @Autowired
    private BeerClient client;

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

    private void log(Page<BeerDTO> beers) {
        log.info("Beers size: {}", beers.getTotalElements());
    }
}