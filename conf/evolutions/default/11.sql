# --- !Ups

CREATE TABLE abstractroyal (
  articleid long primary key auto_increment,
  authortitle varchar,
  author varchar,
  title varchar,
  year long,
  arturl long,
  fulltext varchar,
  textabs varchar);



CREATE TABLE rawroyal (
  articleid long primary key auto_increment,
  url varchar,
  volnum varchar,
  year varchar,
  fulltext varchar);


# --- !Downs

DROP TABLE IF EXISTS abstractroyal;

DROP TABLE IF EXISTS rawroyal;
