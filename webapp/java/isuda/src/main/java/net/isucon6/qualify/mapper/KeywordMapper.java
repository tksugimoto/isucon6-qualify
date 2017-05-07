package net.isucon6.qualify.mapper;

import java.util.List;
import net.isucon6.qualify.domain.Keyword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("keywordMapper")
public interface KeywordMapper {
    void insert(Keyword params);
    List<String> findAllKeywordsOrderByLength();
    boolean exists(String keyword);
}
