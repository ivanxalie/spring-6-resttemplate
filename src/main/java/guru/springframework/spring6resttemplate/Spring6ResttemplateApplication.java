package guru.springframework.spring6resttemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class Spring6ResttemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring6ResttemplateApplication.class, args);
    }

}
