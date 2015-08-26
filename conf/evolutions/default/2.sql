# --- !Ups
    
CREATE TABLE rawarticles (
  articleid long primary key auto_increment,
  fulltext varchar);
  
# --- !Downs

DROP TABLE IF EXISTS rawarticles;