# --- !Ups

CREATE TABLE georefs (
  name_id          long primary key,
  name             varchar,
  status           varchar,
  land_district    varchar,
  crd_projection   varchar,
  crd_north        double,
  crd_east         double,
  crd_datum        varchar,
  crd_latitude     double,
  crd_longitude    double);
  
  # --- !Downs

DROP TABLE IF EXISTS georefs;