# --- !Ups
    
ALTER TABLE abstracthydro ADD COLUMN  arturl long;
  
# --- !Downs

ALTER TABLE abstracthydro DROP COLUMN arturl;