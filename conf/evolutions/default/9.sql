# --- !Ups
    
ALTER TABLE abstracthydro ADD COLUMN  fulltext varchar;
  
# --- !Downs

ALTER TABLE abstracthydro DROP COLUMN fulltext;