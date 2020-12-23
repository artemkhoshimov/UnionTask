package ru.csi;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.csi.service.UnionService;
import ru.csi.service.UnionServiceImpl;

@Configuration
@ComponentScan("ru.csi")
public class ConfigurationClass {

    @Bean
    public UnionService unionService() {
        return new UnionServiceImpl();
    }


}
