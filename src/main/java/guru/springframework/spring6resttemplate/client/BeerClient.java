package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient {
    String BEER_PATH = "/api/v1/beer";
    String BEER_BY_ID = BEER_PATH + "/{beerId}";

    static Builder builder() {
        return new Builder();
    }

    Page<BeerDTO> beers(String name,
                        BeerStyle beerStyle,
                        Boolean showInventory,
                        Integer pageNumber,
                        Integer pageSize);

    default Page<BeerDTO> beers() {
        return beers(null, null, null, null, null);
    }

    default Page<BeerDTO> beers(Builder builder) {
        return beers(
                builder.name,
                builder.style,
                builder.showInventory,
                builder.pageNumber,
                builder.pageSize
        );
    }

    BeerDTO getById(UUID id);

    BeerDTO createBeer(BeerDTO beerDTO);

    BeerDTO updateBeer(BeerDTO dto);

    void delete(UUID id);

    class Builder {
        private String name;
        private BeerStyle style;
        private Boolean showInventory;
        private Integer pageNumber;
        private Integer pageSize;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder style(BeerStyle style) {
            this.style = style;
            return this;
        }

        public Builder showInventory(Boolean showInventory) {
            this.showInventory = showInventory;
            return this;
        }

        public Builder showInventory(Integer pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }
    }
}
