# --- !Ups

ALTER TABLE abstractroyal ALTER COLUMN arturl varchar;

# --- !Downs

ALTER TABLE abstractroyal ALTER COLUMN arturl long;

