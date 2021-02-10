package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@TypeDefs({@TypeDef(name = "json", typeClass = JsonStringType.class)})
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

}