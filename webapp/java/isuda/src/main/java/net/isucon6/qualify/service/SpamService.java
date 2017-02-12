package net.isucon6.qualify.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger log = LoggerFactory.getLogger(SpamService.class);

    public boolean isSpam(String text) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>() {{
            add("content", text);
        }};
        ResponseEntity<Map> res = isupamRestTemplate.postForEntity("/", params, Map.class);
        log.info("receipt response:" + res);
        log.info("response body:" + res.getBody());
        return !Boolean.valueOf(String.valueOf(res.getBody().get("valid")));
    }
}
