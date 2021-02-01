package org.perpetualnetworks.mdcrawlerconsumer.database.query;

import lombok.Value;

@Value
public class DaoOrderByField {

    ResultOrder resultOrder;
    String field;
}
