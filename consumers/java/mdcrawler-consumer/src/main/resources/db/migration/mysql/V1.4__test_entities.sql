
CREATE TABLE mdcrawler_consumer_d.Employee (
  id int(11) NOT NULL AUTO_INCREMENT,
  email varchar(255)  NOT NULL,
  first_name varchar(255)  NOT NULL,
  last_name varchar(255)  NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE mdcrawler_consumer_d.Account (
  id int(11) NOT NULL AUTO_INCREMENT,
  acc_number varchar(255)  NOT NULL,
  employee_id INT(11),
  PRIMARY KEY (id)
);

