--
--CREATE TABLE mdcrawler_consumer_d.api_article (
--  id int NOT NULL AUTO_INCREMENT,
--  title text NOT NULL,
--  source_url text NOT NULL,
--  digital_object_id varchar(128) DEFAULT NULL,
--  refering_url varchar(255) NOT NULL,
--  description text NOT NULL,
--  parse_date datetime NOT NULL,
--  upload_date datetime NOT NULL,
--  parsed int NOT NULL,
--  enriched int NOT NULL,
--  published int NOT NULL,
--  created_date datetime NOT NULL,
--  modified_date datetime NOT NULL,
--  additional_data text NOT NULL,
--  PRIMARY KEY (id)
--)
--

CREATE TABLE mdcrawler_consumer_d.api_keyword (
  id int(11) NOT NULL AUTO_INCREMENT,
  word varchar(128)   NOT NULL,
  created_date datetime(6) NOT NULL,
  modified_date datetime(6) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mdcrawler_consumer_d.api_author (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255)  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mdcrawler_consumer_d.api_articlefile (
 id int NOT NULL AUTO_INCREMENT,
 file_name varchar(255)   NOT NULL,
 url varchar(255)   NOT NULL,
 download_url varchar(255)   NOT NULL,
 digital_object_id varchar(128)   NOT NULL,
 description longtext   NOT NULL,
 refering_url varchar(255)   NOT NULL,
 size double DEFAULT NULL,
 article_id int(11) NOT NULL,
 PRIMARY KEY (id),
 KEY api_articlefile_article_id_722e53a2_fk_api_article_id (article_id),
 CONSTRAINT api_articlefile_article_id_722e53a2_fk_api_article_id FOREIGN KEY (article_id) REFERENCES api_article (id)
 );
