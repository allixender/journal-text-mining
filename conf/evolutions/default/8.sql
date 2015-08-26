# --- !Ups

CREATE TABLE procedures (
  identifier                varchar primary key,
  xmltext                   varchar);

CREATE TABLE features (
  identifier                varchar primary key,
  xmltext                   varchar);
  
CREATE TABLE damembers (
  identifier                varchar primary key,
  xmltext                   varchar);

CREATE TABLE observations (
  identifier                varchar primary key,
  procedureid               varchar,
  featureid                 varchar,
  phenomenonid              varchar,
  xmltext                   varchar);
  
# --- !Downs

DROP TABLE IF EXISTS damembers;
DROP TABLE IF EXISTS features;
DROP TABLE IF EXISTS procedures;