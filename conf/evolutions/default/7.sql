# --- !Ups

CREATE TABLE terms (
  termid                    long primary key auto_increment,
  collection                varchar,
  preflabel                 varchar,
  term_en                   varchar,
  term_en_origin            varchar,
  term_en_related           varchar,
  term_mi                   varchar,
  term_en_description       varchar,
  term_mi_description_translation varchar,
  term_mi_origin            varchar,
  term_mi_related           varchar,
  bib_citation              varchar,
  subjects                  varchar,
  doc_type                  varchar,
  ucd_number                varchar,
  nzms260_map_no            varchar,
  ext_links                 varchar,
  notes                     varchar,
  rec_loaded_date           varchar,
  rec_loaded_by             varchar,
  rec_reviewed_by           varchar,
  last_edit                 timestamp);

# --- !Downs

DROP TABLE IF EXISTS terms;