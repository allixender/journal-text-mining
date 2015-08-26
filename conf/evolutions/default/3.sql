# --- !Ups
    
ALTER TABLE abstracthydro ADD COLUMN  author varchar;
ALTER TABLE abstracthydro ADD COLUMN  title varchar;
  
# --- !Downs

ALTER TABLE abstracthydro DROP COLUMN title;
ALTER TABLE abstracthydro DROP COLUMN author;