package net.isucon6.qualify.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SpamService {
    @Autowired
    private RestTemplate isupamRestTemplate;

    public boolean isSpam(String text) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>() {{
            add("content", text);
        }};
        ResponseEntity<Map> res = isupamRestTemplate.postForEntity("/", params, Map.class);
        return !Boolean.valueOf(String.valueOf(res.getBody().get("valid")));
    }
}
