# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table booking (
  id                        varchar(255) not null,
  date_of_booking           timestamp,
  day_of_booking_start      timestamp,
  day_of_booking_end        timestamp,
  user_id                   bigint,
  transaction_id            varchar(255),
  cabin                     varchar(255),
  nr_of_persons             integer,
  constraint pk_booking primary key (id))
;

create table user (
  id                        bigint not null,
  auth_token                varchar(255),
  email_address             varchar(256) not null,
  sha_password              varbinary(64) not null,
  full_name                 varchar(256) not null,
  creation_date             timestamp not null,
  constraint uq_user_email_address unique (email_address),
  constraint pk_user primary key (id))
;

create sequence booking_seq;

create sequence user_seq;

alter table booking add constraint fk_booking_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_booking_user_1 on booking (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists booking;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists booking_seq;

drop sequence if exists user_seq;

