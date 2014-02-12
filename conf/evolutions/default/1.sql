# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table booking (
  id                        varchar(255) not null,
  date_of_booking           timestamp,
  day_of_booking_start      timestamp,
  day_of_booking_end        timestamp,
  user_id                   varchar(255),
  transaction_id            varchar(255),
  cabin                     varchar(255),
  constraint pk_booking primary key (id))
;

create sequence booking_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists booking;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists booking_seq;

