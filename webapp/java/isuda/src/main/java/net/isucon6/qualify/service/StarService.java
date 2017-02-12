package net.isucon6.qualify.service;

import java.util.List;
import java.util.stream.Collectors;

import net.isucon6.qualify.response.StarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StarService {

    @Autowired
    private RestTemplate isutarRestTemplate;

    public List<String> fetch(String keyword) {
        return isutarRestTemplate.getForObject("/stars?keyword=" + keyword, StarResponse.class).getStars()
                .stream()
                .map(StarResponse.Star::getUserName)
                .collect(Collectors.toList());
    }

    public void initialize() {
        isutarRestTemplate.getForObject("/initialize", String.class);
    }
}
