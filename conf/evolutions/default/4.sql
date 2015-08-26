# --- !Ups
    
ALTER TABLE rawarticles ADD COLUMN  url varchar;
ALTER TABLE rawarticles ADD COLUMN  volnum varchar;
ALTER TABLE rawarticles ADD COLUMN  year varchar;

ALTER TABLE abstracthydro ADD COLUMN  year long;
  
# --- !Downs

ALTER TABLE abstracthydro DROP COLUMN year;

ALTER TABLE rawarticles DROP COLUMN year;
ALTER TABLE rawarticles DROP COLUMN volnum;
ALTER TABLE rawarticles DROP COLUMN url;