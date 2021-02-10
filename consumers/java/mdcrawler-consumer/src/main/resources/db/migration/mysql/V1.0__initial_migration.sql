CREATE SCHEMA mdcrawler_consumer_d;

CREATE TABLE mdcrawler_consumer_d.api_article (
  id int NOT NULL AUTO_INCREMENT,
  title text NOT NULL,
  source_url text NOT NULL,
  digital_object_id varchar(128) DEFAULT NULL,
  refering_url varchar(255) NOT NULL,
  description text NOT NULL,
  parse_date datetime NOT NULL,
  upload_date datetime NOT NULL,
  parsed int NOT NULL,
  enriched int NOT NULL,
  published int NOT NULL,
  created_date datetime NOT NULL,
  modified_date datetime NOT NULL,
  additional_data text NOT NULL,
  PRIMARY KEY (id)
);