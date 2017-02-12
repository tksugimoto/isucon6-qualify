package net.isucon6.qualify;

import java.nio.charset.Charset;
import java.util.Arrays;

import net.isucon6.qualify.advice.AuthenticateInterceptor;
import net.isucon6.qualify.advice.SetNameInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class IsudaConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private SetNameInterceptor setNameInterceptor;
    @Autowired
    private AuthenticateInterceptor authenticateInterceptor;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(setNameInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authenticateInterceptor).addPathPatterns("/**");
    }

    @Bean
    public RestTemplate isutarRestTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("http://localhost:5001").build();
    }

    @Bean
    public RestTemplate isupamRestTemplate(RestTemplateBuilder builder) {
        RestTemplate template = builder.rootUri("http://localhost:5050").build();
        template.setMessageConverters(Arrays.asList(
                new StringHttpMessageConverter(Charset.forName("UTF-8")),
                new FormHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()));
        return template;
    }
}
