package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DtoToEntityConverter {

    public ArticleEntity convert(Article article) {
        return ArticleEntity.builder()
                .title(article.getTitle())
                .sourceUrl(article.getSourceUrl())
                .digitalObjectId(Objects.nonNull(article.getDigitalObjectId())
                        ? article.getDigitalObjectId() : "unavailable")
                .description(Objects.nonNull(article.getDescription())
                        ? article.getDescription() : "unavailable")
                .parseDate(parseTime(article.getParseDate()))
                .uploadDate(parseTime(article.getUploadDate()))
                .createdDate(parseTime(article.getCreatedDate()))
                .modifiedDate(parseTime(article.getModifiedDate()))
                .referingUrl(Objects.nonNull(article.getReferingUrl())
                        ? article.getReferingUrl() : "unavailable")
                .enriched(Objects.nonNull(article.getEnriched())
                        ? article.getEnriched() : false)
                .published(Objects.nonNull(article.getPublished())
                        ? article.getPublished() : false)
                .parsed(Objects.nonNull(article.getParsed())
                        ? article.getParsed() : false)
                .additionalData(Objects.nonNull(article.getAdditionalData())
                        ? article.getAdditionalData() : Article.AdditionalData.builder().build())
                //related items must go through individual repositories
                //keywords
                //authors
                //files
                .build();
    }

    public ArticleFileEntity convert(ArticleFile articleFile) {
        return ArticleFileEntity.builder()
                .fileName(articleFile.getFileName())
                .url(Objects.nonNull(articleFile.getUrl())
                        ? articleFile.getUrl() : "unvailable")
                .downloadUrl(Objects.nonNull(articleFile.getDownloadUrl())
                        ? articleFile.getDownloadUrl() : "unavailable")
                .digitalObjectId(Objects.nonNull(articleFile.getDigitalObjectId())
                        ? articleFile.getDigitalObjectId() : "unavailable")
                .description(Objects.nonNull(articleFile.getFileDescription())
                        ? articleFile.getFileDescription() : "unavailable")
                .referingUrl(Objects.nonNull(articleFile.getReferingUrl())
                        ? articleFile.getReferingUrl() : "unavailable")
                .size(parseFileSize(articleFile.getSize()))
                //related items must go through individual repositories
                // keywords
                .build();
    }

    public KeywordEntity convert(String keyword) {
        return KeywordEntity.builder()
                .word(keyword)
                .createdDate(new Date())
                .modifiedDate(new Date())
                .build();
    }

    public AuthorEntity convert(Author author) {
        return AuthorEntity.builder()
                .name(author.getName())
                .build();
    }

    @SneakyThrows
    private Date parseTime(String dateString) {
        if (dateString == null) {
            return new Date();
        }
        try {
            return new SimpleDateFormat(Constants.Time.IsoPattern).parse(dateString);
        } catch (ParseException ignored) {
            // ignored
        }
        try {
            return new SimpleDateFormat(Constants.Time.AlternatePattern).parse(dateString);
        } catch (ParseException ignored) {
            // ignored
        }
        try {
            final LocalDate parse = LocalDate.parse(dateString);
            return new Date(parse.toEpochDay());
        } catch (Exception ignored) {
            // ignored
        }
        log.warn("unable to parse date string: " + dateString + " returning new date");
        return new Date();
    }

    private Double parseFileSize(String fileSize) {
        if (fileSize == null) {
            return 1d;
        }
        //TODO: find better representation
        Double size = Double.valueOf("1");
        final Map<String, Double> units = new TreeMap<>();
        units.put("TB", Double.valueOf("1024000000000"));
        units.put("GB", Double.valueOf("1024000000"));
        units.put("MB", Double.valueOf("1024000"));
        units.put("kB", Double.valueOf("1024"));
        units.put("B", Double.valueOf("1"));
        for (String unit : units.keySet()) {
            final String regex = "^([0-9.]+)\\s?" + unit + "$";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(fileSize);
            if (matcher.matches()) {
                Double multiplier = units.get(unit);
                Float value = Float.valueOf(fileSize.replace(unit, ""));
                size = value * multiplier;
                System.out.println("size is: " + size);
                break;
            }
        }
        return size;
    }
}
