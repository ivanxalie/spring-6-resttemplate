package guru.springframework.spring6resttemplate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonIgnoreProperties(value = "pageable", ignoreUnknown = true)
public class RestPageImpl extends PageImpl<BeerDTO> {

    public RestPageImpl(List<BeerDTO> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @JsonCreator
    public RestPageImpl(
            @JsonProperty("content") List<BeerDTO> content,
            @JsonProperty("page") PageDetails pageDetails
    ) {
        super(content, PageRequest.of(pageDetails.number(), pageDetails.size()), pageDetails.totalElements());
    }

    public record PageDetails(int size, int number, long totalElements) {
        @JsonCreator
        public PageDetails(
                @JsonProperty("size") int size,
                @JsonProperty("number") int number,
                @JsonProperty("totalElements") long totalElements
        ) {
            this.size = size;
            this.number = number;
            this.totalElements = totalElements;
        }
    }
}
