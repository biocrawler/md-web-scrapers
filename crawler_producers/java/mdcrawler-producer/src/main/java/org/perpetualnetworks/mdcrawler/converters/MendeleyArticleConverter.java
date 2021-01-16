package org.perpetualnetworks.mdcrawler.converters;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.models.Author;
import org.perpetualnetworks.mdcrawler.scrapers.dto.MendeleyResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

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
            if (keywordList != null) {
                keywords.addAll(parseKeywordList(keywordList));
            }
        }
        return keywords;
    }

    private List<String> parseKeywordList(List<String> stringlist) {
        List<String> keywords = new ArrayList<>();
        for (String entry : stringlist) {
            try {
                keywords.add(Arrays.toString(entry.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                log.info("keyword add failure for entry:" + entry);
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
        if (result.getAssetTypes() != null) {
            details.addAll(result.getAssetTypes());
        }
        return Article.AdditionalData.builder().labDetails(details).build();
    }

    private Set<Author> parseAuthors(MendeleyResponse.Result result) {
        Set<Author> authorSet = new HashSet<>();
        try {
            if (nonNull(result.getAuthors())) {
               authorSet.addAll(convertAuthors(result.getAuthors()));
            }
            if (nonNull(result.getAuthorEntities())) {
                authorSet.addAll(convertAuthors(result.getAuthorEntities()));
            }
        } catch (Exception e) {
            log.error("could not parse authors for result: " + result);
        }
        return authorSet;
    }

    private Set<Author> convertAuthors(List<MendeleyResponse.Author> authors) {
        return authors.stream()
             .map(MendeleyResponse.Author::getName)
             .filter(StringUtils::isNotBlank)
             .map(name -> Author.builder().name(name).build())
             .collect(Collectors.toSet());
    }


    public static String removeTags(String string) {
        if (StringUtils.isBlank(string)) {
            return string;
        }
        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

}
