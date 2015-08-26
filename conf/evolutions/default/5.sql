# --- !Ups
    
CREATE TABLE metadata (
  metadataid long primary key,
  xml varchar);

# --- !Downs

DROP TABLE IF EXISTS metadata;