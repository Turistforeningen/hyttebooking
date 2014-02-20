# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table booking (
  id                        bigint not null,
  guest_id                  bigint,
  date_from                 timestamp,
  date_to                   timestamp,
  user_id                   varchar(255),
  transaction_id            varchar(255),
  cabin_id                  bigint,
  constraint pk_booking primary key (id))
;

create table CABIN (
  CABIN_TYPE                varchar(31) not null,
  id                        bigint not null,
  constraint pk_CABIN primary key (id))
;

create sequence booking_seq;

create sequence CABIN_seq;

alter table booking add constraint fk_booking_cabin_1 foreign key (cabin_id) references CABIN (id) on delete restrict on update restrict;
create index ix_booking_cabin_1 on booking (cabin_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists booking;

drop table if exists CABIN;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists booking_seq;

drop sequence if exists CABIN_seq;

