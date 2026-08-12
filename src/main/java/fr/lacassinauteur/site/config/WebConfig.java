package fr.lacassinauteur.site.config;

import fr.lacassinauteur.site.shared.infrastructure.stockage.StockageImagesProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StockageImagesProperties stockageImagesProperties;

    public WebConfig(StockageImagesProperties stockageImagesProperties) {
        this.stockageImagesProperties = stockageImagesProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String cheminAbsolu = Path.of(stockageImagesProperties.getChemin()).toAbsolutePath().normalize().toString();

        registry.addResourceHandler(stockageImagesProperties.getPrefixeUrl() + "/**")
                .addResourceLocations("file:" + cheminAbsolu + "/");
    }
}
