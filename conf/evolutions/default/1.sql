# --- !Ups
    
CREATE TABLE abstracthydro (
  articleid long primary key auto_increment,
  authortitle varchar,
  textabs varchar);
  
# --- !Downs

DROP TABLE IF EXISTS abstracthydro;