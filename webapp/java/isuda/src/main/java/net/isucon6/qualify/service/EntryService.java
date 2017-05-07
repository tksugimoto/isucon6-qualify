package net.isucon6.qualify.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.isucon6.qualify.domain.Entry;
import net.isucon6.qualify.dto.EntryDto;
import net.isucon6.qualify.exception.NotFoundException;
import net.isucon6.qualify.mapper.EntryMapper;
import net.isucon6.qualify.mapper.KeywordMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

@Service
public class EntryService {
    private final EntryMapper entryMapper;
    private final ModelMapper modelMapper;
    private final StarService starService;
    private final KeywordMapper keywordMapper;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(EntryService.class);

    @Autowired
    public EntryService(EntryMapper entryMapper, ModelMapper modelMapper, StarService starService, KeywordMapper keywordMapper) {
        this.entryMapper = entryMapper;
        this.modelMapper = modelMapper;
        this.starService = starService;
        this.keywordMapper = keywordMapper;
    }

    private Long currentTime() {
        Long mills = System.nanoTime() / 1000 / 1000;
        return mills % 10000;
    }

    class HtmlifyService {
        private final List<String> keywords;

        public HtmlifyService() {
            log.info("findAllKeywordsOrderByLength start: " + currentTime());
            keywords = keywordMapper.findAllKeywordsOrderByLength();
            log.info("findAllKeywordsOrderByLength end:" + currentTime());
        }

        public String htmlify(final String content) {
            if (StringUtils.isEmpty(content)) {
                return "";
            }


            log.info("htmlify Pattern.compile start: " + currentTime());
            Pattern pattern = Pattern.compile(keywords.stream()
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|", "(", ")")));

            log.info("htmlify Pattern.compile end: " + currentTime());

            Matcher matcher = pattern.matcher(content);

            log.info("htmlify pattern.matcher end: " + currentTime());

            Map<String, String> kw2sha = keywords.stream()
                    .collect(Collectors.toMap(
                            keyword -> keyword,
                            keyword -> "isuda_" + DigestUtils.sha1Hex(keyword)
                    ));

            log.info("htmlify kw2sha create end: " + currentTime());

            StringBuffer sbKw2Sha = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sbKw2Sha, kw2sha.get(matcher.group()));
            }
            log.info("htmlify while (matcher.find()) end: " + currentTime());
            String result = matcher.appendTail(sbKw2Sha).toString();
            try {
                for (Map.Entry<String, String> e : kw2sha.entrySet()) {
                    String kw = e.getKey();
                    String hash = e.getValue();
                    String link = String.format("<a href=\"%s\">%s</a>",
                            String.format("/keyword/%s", URLEncoder.encode(kw, "UTF-8")),
                            HtmlUtils.htmlEscape(kw, "UTF-8")
                    );
                    Matcher m = Pattern.compile(hash).matcher(result);
                    result = m.replaceAll(link);
                }
            } catch (UnsupportedEncodingException e) {
                log.warn("Failed to replace keyword.");
            }

            return result.replace("\n", "<br />");
        }
    }

    public List<EntryDto> findHtmlEntries(int perPage, int currentPage) {
        HtmlifyService htmlifyService = new HtmlifyService();
        Map<String, Integer> params = new HashMap<>();
        params.put("perPage", perPage);
        params.put("offset", (perPage * (currentPage - 1)));
        return entryMapper.findByPageNum(params).stream()
                .map(e -> {
                    EntryDto ed = modelMapper.map(e, EntryDto.class);
                    ed.setHtml(htmlifyService.htmlify(e.getDescription()));

                    ed.setStars(starService.fetch(e.getKeyword()));
                    return ed;
                })
                .collect(Collectors.toList());
    }

    public EntryDto findHtmlByKeyword(String keyword) {
        log.info("searching for:" + currentTime() + ", " + keyword);
        Entry entry = entryMapper.findByKeyword(keyword);
        log.info("got entry:" + currentTime() + ", " + entry);
        if (entry == null) throw new NotFoundException();

        EntryDto entryDto = modelMapper.map(entry, EntryDto.class);
        HtmlifyService htmlifyService = new HtmlifyService();
        log.info("htmlify start:" + currentTime() + ", " + keyword);
        entryDto.setHtml(htmlifyService.htmlify(entry.getDescription()));
        log.info("htmlify end:" + currentTime() + ", " + keyword);
        entryDto.setStars(starService.fetch(entry.getKeyword()));
        log.info("starService.fetch end:" + currentTime() + ", " + keyword);
        return entryDto;
    }

    public Entry findByKeyword(String keyword) {
        return entryMapper.findByKeyword(keyword);
    }

    public void delete(String keyword) {
        entryMapper.delete(keyword);
    }
}
