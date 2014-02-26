# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bed (
  id                        bigint not null,
  large_cabin_id            bigint,
  constraint pk_bed primary key (id))
;

create table booking (
  id                        bigint not null,
  date_from                 timestamp,
  date_to                   timestamp,
  date_of_order             timestamp,
  guest_id                  bigint,
  payment_id                bigint,
  cabin_id                  bigint,
  constraint pk_booking primary key (id))
;

create table cabin (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  constraint pk_cabin primary key (id))
;

create table guest (
  id                        bigint not null,
  constraint pk_guest primary key (id))
;

create table payment (
  id                        bigint not null,
  amount                    double,
  date                      date,
  constraint pk_payment primary key (id))
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


create table bed_booking (
  bed_id                         bigint not null,
  booking_id                     bigint not null,
  constraint pk_bed_booking primary key (bed_id, booking_id))
;

create table booking_bed (
  booking_id                     bigint not null,
  bed_id                         bigint not null,
  constraint pk_booking_bed primary key (booking_id, bed_id))
;
create sequence bed_seq;

create sequence booking_seq;

create sequence cabin_seq;

create sequence guest_seq;

create sequence payment_seq;

create sequence user_seq;

alter table bed add constraint fk_bed_largeCabin_1 foreign key (large_cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_bed_largeCabin_1 on bed (large_cabin_id);
alter table booking add constraint fk_booking_guest_2 foreign key (guest_id) references guest (id) on delete restrict on update restrict;
create index ix_booking_guest_2 on booking (guest_id);
alter table booking add constraint fk_booking_payment_3 foreign key (payment_id) references payment (id) on delete restrict on update restrict;
create index ix_booking_payment_3 on booking (payment_id);
alter table booking add constraint fk_booking_cabin_4 foreign key (cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_booking_cabin_4 on booking (cabin_id);



alter table bed_booking add constraint fk_bed_booking_bed_01 foreign key (bed_id) references bed (id) on delete restrict on update restrict;

alter table bed_booking add constraint fk_bed_booking_booking_02 foreign key (booking_id) references booking (id) on delete restrict on update restrict;

alter table booking_bed add constraint fk_booking_bed_booking_01 foreign key (booking_id) references booking (id) on delete restrict on update restrict;

alter table booking_bed add constraint fk_booking_bed_bed_02 foreign key (bed_id) references bed (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists bed;

drop table if exists bed_booking;

drop table if exists booking;

drop table if exists booking_bed;

drop table if exists cabin;

drop table if exists guest;

drop table if exists payment;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists bed_seq;

drop sequence if exists booking_seq;

drop sequence if exists cabin_seq;

drop sequence if exists guest_seq;

drop sequence if exists payment_seq;

drop sequence if exists user_seq;

