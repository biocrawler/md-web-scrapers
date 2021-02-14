package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import lombok.SneakyThrows;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DtoToEntityConverter {

    public ArticleEntity convert(Article article) {
        return ArticleEntity.builder()
                .title(article.getTitle())
                .sourceUrl(article.getSourceUrl())
                .digitalObjectId(article.getDigitalObjectId())
                .description(article.getDescription())
                .parseDate(parseTime(article.getParseDate()))
                .uploadDate(parseTime(article.getUploadDate()))
                .createdDate(parseTime(article.getCreatedDate()))
                .modifiedDate(parseTime(article.getModifiedDate()))
                .referingUrl(article.getReferingUrl())
                .enriched(article.getEnriched())
                .published(article.getPublished())
                .parsed(article.getParsed())
                .additionalData(article.getAdditionalData())
                //related items must go through individual repositories
                //keywords
                //authors
                //files
                .build();
    }

    public ArticleFileEntity convert(ArticleFile articleFile) {
        return ArticleFileEntity.builder()
                .fileName(articleFile.getFileName())
                .url(articleFile.getUrl())
                .downloadUrl(articleFile.getDownloadUrl())
                .digitalObjectId(articleFile.getDigitalObjectId())
                .description(articleFile.getFileDescription())
                .referingUrl(articleFile.getReferingUrl())
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
        return new SimpleDateFormat(Constants.Time.IsoPattern).parse(dateString);
    }

    private Double parseFileSize(String fileSize) {
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
