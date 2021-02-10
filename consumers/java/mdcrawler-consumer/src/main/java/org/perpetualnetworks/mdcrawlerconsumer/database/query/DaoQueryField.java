package org.perpetualnetworks.mdcrawlerconsumer.database.query;

import lombok.Value;

@Value
public class DaoQueryField {

    OpMatcher opMatcher;
    Object value;
    String fieldName;
}
