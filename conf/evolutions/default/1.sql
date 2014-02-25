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
  guest_id                  bigint,
  payment_id                bigint,
  cabin_id                  bigint,
  beds_id                   bigint,
  constraint pk_booking primary key (id))
;

create table cabin (
  dtype                     varchar(10) not null,
  id                        bigint not null,
  constraint pk_cabin primary key (id))
;

create table guest (
  id                        bigint not null,
  booking_id                bigint,
  constraint pk_guest primary key (id))
;

create table payment (
  id                        bigint not null,
  amount                    double,
  date                      date,
  constraint pk_payment primary key (id))
;

create sequence bed_seq;

create sequence booking_seq;

create sequence cabin_seq;

create sequence guest_seq;

create sequence payment_seq;

alter table bed add constraint fk_bed_largeCabin_1 foreign key (large_cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_bed_largeCabin_1 on bed (large_cabin_id);
alter table booking add constraint fk_booking_guest_2 foreign key (guest_id) references guest (id) on delete restrict on update restrict;
create index ix_booking_guest_2 on booking (guest_id);
alter table booking add constraint fk_booking_payment_3 foreign key (payment_id) references payment (id) on delete restrict on update restrict;
create index ix_booking_payment_3 on booking (payment_id);
alter table booking add constraint fk_booking_cabin_4 foreign key (cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_booking_cabin_4 on booking (cabin_id);
alter table booking add constraint fk_booking_beds_5 foreign key (beds_id) references bed (id) on delete restrict on update restrict;
create index ix_booking_beds_5 on booking (beds_id);
alter table guest add constraint fk_guest_booking_6 foreign key (booking_id) references booking (id) on delete restrict on update restrict;
create index ix_guest_booking_6 on guest (booking_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists bed;

drop table if exists booking;

drop table if exists cabin;

drop table if exists guest;

drop table if exists payment;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists bed_seq;

drop sequence if exists booking_seq;

drop sequence if exists cabin_seq;

drop sequence if exists guest_seq;

drop sequence if exists payment_seq;

