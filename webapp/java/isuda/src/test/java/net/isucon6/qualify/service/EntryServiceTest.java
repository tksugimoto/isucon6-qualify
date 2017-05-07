package net.isucon6.qualify.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;

import net.isucon6.qualify.domain.Entry;
import net.isucon6.qualify.mapper.EntryMapper;
import net.isucon6.qualify.mapper.KeywordMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EntryServiceTest {
    @Autowired
    private EntryMapper entryMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StarService starService;
    @Mock
    private KeywordMapper keywordMapper;

    @Test
    public void testHtmlify() throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String description = "123の説明です. 以下の文字列はリンクのはずです. ジゴロ / re[ge]xp / /html/escape";
        Mockito.when(keywordMapper.findAllKeywordsOrderByLength()).thenReturn(new ArrayList<String>() {{
            add("123");
            add("ジゴロ");
            add("re[ge]xp");
            add("/html/escape");
        }});

        EntryService entryService = new EntryService(entryMapper, modelMapper, starService, keywordMapper);
        Constructor constructor = EntryService.HtmlifyService.class.getDeclaredConstructor(EntryService.class);
        EntryService.HtmlifyService htmlifyService = (EntryService.HtmlifyService) constructor.newInstance(entryService);
        String actual = htmlifyService.htmlify(description);

        assertThat(actual, is("<a href=\"/keyword/123\">123</a>の説明です. 以下の文字列はリンクのはずです. <a href=\"/keyword/%E3%82%B8%E3%82%B4%E3%83%AD\">ジゴロ</a> / <a href=\"/keyword/re%5Bge%5Dxp\">re[ge]xp</a> / <a href=\"/keyword/%2Fhtml%2Fescape\">/html/escape</a>"));
    }
}
