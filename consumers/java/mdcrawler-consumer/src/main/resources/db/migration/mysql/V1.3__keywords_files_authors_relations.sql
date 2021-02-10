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

CREATE TABLE mdcrawler_consumer_d.api_article_authors (
  id int(11) NOT NULL AUTO_INCREMENT,
  article_id int(11) NOT NULL,
  author_id int(11) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY api_article_authors_article_id_author_id_26e03e96_uniq (article_id,author_id),
  KEY api_article_authors_author_id_a8354929_fk_api_author_id (author_id),
  CONSTRAINT api_article_authors_article_id_ff860786_fk_api_article_id FOREIGN KEY (article_id) REFERENCES api_article (id),
  CONSTRAINT api_article_authors_author_id_a8354929_fk_api_author_id FOREIGN KEY (author_id) REFERENCES api_author (id)
);

 CREATE TABLE mdcrawler_consumer_d.api_article_keywords (
   id int(11) NOT NULL AUTO_INCREMENT,
   article_id int(11) NOT NULL,
   keyword_id int(11) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE KEY api_article_keywords_article_id_keyword_id_942ea8e1_uniq (article_id,keyword_id),
   KEY api_article_keywords_keyword_id_4fb8a1f5_fk_api_keyword_id (keyword_id),
   CONSTRAINT api_article_keywords_article_id_e1d0f380_fk_api_article_id FOREIGN KEY (article_id) REFERENCES api_article (id),
   CONSTRAINT api_article_keywords_keyword_id_4fb8a1f5_fk_api_keyword_id FOREIGN KEY (keyword_id) REFERENCES api_keyword (id)
);

CREATE TABLE mdcrawler_consumer_d.api_articlefile_keywords (
  id int(11) NOT NULL AUTO_INCREMENT,
  articlefile_id int(11) NOT NULL,
  keyword_id int(11) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY api_articlefile_keywords_articlefile_id_keyword_id_7457c31c_uniq (articlefile_id,keyword_id),
  KEY api_articlefile_keywords_keyword_id_3d1653f6_fk_api_keyword_id (keyword_id),
  CONSTRAINT api_articlefile_keyw_articlefile_id_f01d46ce_fk_api_artic FOREIGN KEY (articlefile_id) REFERENCES api_articlefile (id),
  CONSTRAINT api_articlefile_keywords_keyword_id_3d1653f6_fk_api_keyword_id FOREIGN KEY (keyword_id) REFERENCES api_keyword (id)
);