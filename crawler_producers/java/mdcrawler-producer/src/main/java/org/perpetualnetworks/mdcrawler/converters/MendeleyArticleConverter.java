package org.perpetualnetworks.mdcrawler.converters;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.models.Author;
import org.perpetualnetworks.mdcrawler.scrapers.dto.MendeleyResponse;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MendeleyArticleConverter {

    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

    public Article convert(MendeleyResponse.Result result) {
        return Article.builder()
                .title(removeTags(result.getContainerTitle()))
                .digitalObjectId(result.getDoi() == null ? result.getDoi() : result.getExternalId())
                .description(removeTags(result.getContainerDescription()))
                .sourceUrl(result.getContainerURI())
                .uploadDate(result.getDateCreated())
                .parseDate(Date.from(Instant.now()).toString())
                .keywords(parseKeywords(result))
                .enriched(false)
                .additionalData(parseAddtionalData(result))
                .authors(parseAuthors(result))
                .build();
    }

    private HashSet<String> parseKeywords(MendeleyResponse.Result result) {
        HashSet<String> keywords = new HashSet<>();
        for (List<String> keywordList : Arrays.asList(result.getContainerKeywords(),
                result.getSubjectAreas(), result.getExternalSubjectAreas())) {
            try {
                keywords.addAll(keywordList);
            } catch (Exception e) {
                log.info("keywordlist add failure for list in result: " + result);
            }
        }
        return keywords;
    }

    private Article.AdditionalData parseAddtionalData(MendeleyResponse.Result result) {
        List<String> details = new ArrayList<>();
        if (result.getMethod() != null) {
            details.add(result.getMethod());
        }
        if (result.getSnippets() != null) {
            details.addAll(result.getSnippets());
        }
        return Article.AdditionalData.builder().labDetails(details).build();
    }

    private Set<Author> parseAuthors(MendeleyResponse.Result result) {
        try {
            return result.getAuthors().stream()
                    .map(MendeleyResponse.Author::getName)
                    .filter(StringUtils::isNotBlank)
                    .map(name -> Author.builder().name(name).build())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("could not parse authors for result: " + result);
        }
        return Collections.emptySet();
    }



    public static String removeTags(String string) {
        if (StringUtils.isBlank(string)) {
            return string;
        }
        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

}
