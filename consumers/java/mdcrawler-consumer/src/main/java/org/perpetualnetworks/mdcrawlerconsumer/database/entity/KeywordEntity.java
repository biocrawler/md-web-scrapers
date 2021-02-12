package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "api_keyword", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class KeywordEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable =false)
    Long id;

    @Column(name = "word")
    String word;

    @Column(name = "created_date")
    String createdDate;

    @Column(name = "modified_date")
    String modifiedDate;

    @OneToMany(mappedBy = "keywordEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<ArticleKeywordRelationEntity> articleRelations = new ArrayList<>();

    @OneToMany(mappedBy = "keywordEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<FileKeywordRelationEntity> fileRelations = new ArrayList<>();
}