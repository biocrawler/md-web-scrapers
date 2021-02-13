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
@Table(name = "api_author", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class AuthorEntity extends BaseEntity {

    @Column(name = "name")
    String name;

    @OneToMany(mappedBy = "authorEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<ArticleAuthorRelationEntity> articleRelations = new ArrayList<>();

}
