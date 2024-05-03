package co.com.FacialRecognition.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class Configuracion implements WebMvcConfigurer {

    private final String baseUrl;

    public Configuracion(@Value("${springfox.documentation.swagger-ui.base-url:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(baseUrl + "/swagger-ui/")
                .setViewName("forward:" + baseUrl + "/swagger-ui/index.html");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');

        registry.addResourceHandler(baseUrl + "/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

}