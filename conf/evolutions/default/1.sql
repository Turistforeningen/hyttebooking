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
  time_of_booking           bigint,
  user_id                   bigint,
  status                    integer,
  payment_id                bigint,
  small_cabin_id            bigint,
  constraint pk_booking primary key (id))
;

create table cabin (
  DTYPE                     varchar(31) not null,
  id                        bigint not null,
  name                      varchar(255),
  price_for_cabin_id        bigint,
  constraint pk_cabin primary key (id))
;

create table guest (
  id                        bigint not null,
  payment_id                bigint,
  price_category_id         bigint,
  is_member                 boolean,
  nr                        integer,
  constraint pk_guest primary key (id))
;

create table payment (
  id                        bigint not null,
  amount                    double,
  date                      timestamp,
  transaction_id            varchar(255),
  constraint pk_payment primary key (id))
;

create table price (
  id                        bigint not null,
  guest_type                varchar(255),
  age_range                 varchar(255),
  non_member_price          double,
  member_price              double,
  is_minor                  boolean,
  constraint pk_price primary key (id))
;

create table user (
  id                        bigint not null,
  auth_token                varchar(255),
  email_address             varchar(256) not null,
  full_name                 varchar(256) not null,
  is_admin                  boolean,
  creation_date             timestamp not null,
  constraint pk_user primary key (id))
;


create table bed_booking (
  bed_id                         bigint not null,
  booking_id                     bigint not null,
  constraint pk_bed_booking primary key (bed_id, booking_id))
;

create table cabin_price (
  cabin_id                       bigint not null,
  price_id                       bigint not null,
  constraint pk_cabin_price primary key (cabin_id, price_id))
;
create sequence bed_seq;

create sequence booking_seq;

create sequence cabin_seq;

create sequence guest_seq;

create sequence payment_seq;

create sequence price_seq;

create sequence user_seq;

alter table bed add constraint fk_bed_largeCabin_1 foreign key (large_cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_bed_largeCabin_1 on bed (large_cabin_id);
alter table booking add constraint fk_booking_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_booking_user_2 on booking (user_id);
alter table booking add constraint fk_booking_payment_3 foreign key (payment_id) references payment (id) on delete restrict on update restrict;
create index ix_booking_payment_3 on booking (payment_id);
alter table booking add constraint fk_booking_smallCabin_4 foreign key (small_cabin_id) references cabin (id) on delete restrict on update restrict;
create index ix_booking_smallCabin_4 on booking (small_cabin_id);
alter table cabin add constraint fk_cabin_priceForCabin_5 foreign key (price_for_cabin_id) references price (id) on delete restrict on update restrict;
create index ix_cabin_priceForCabin_5 on cabin (price_for_cabin_id);
alter table guest add constraint fk_guest_payment_6 foreign key (payment_id) references payment (id) on delete restrict on update restrict;
create index ix_guest_payment_6 on guest (payment_id);
alter table guest add constraint fk_guest_priceCategory_7 foreign key (price_category_id) references price (id) on delete restrict on update restrict;
create index ix_guest_priceCategory_7 on guest (price_category_id);



alter table bed_booking add constraint fk_bed_booking_bed_01 foreign key (bed_id) references bed (id) on delete restrict on update restrict;

alter table bed_booking add constraint fk_bed_booking_booking_02 foreign key (booking_id) references booking (id) on delete restrict on update restrict;

alter table cabin_price add constraint fk_cabin_price_cabin_01 foreign key (cabin_id) references cabin (id) on delete restrict on update restrict;

alter table cabin_price add constraint fk_cabin_price_price_02 foreign key (price_id) references price (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists bed;

drop table if exists bed_booking;

drop table if exists booking;

drop table if exists cabin;

drop table if exists guest;

drop table if exists payment;

drop table if exists price;

drop table if exists cabin_price;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists bed_seq;

drop sequence if exists booking_seq;

drop sequence if exists cabin_seq;

drop sequence if exists guest_seq;

drop sequence if exists payment_seq;

drop sequence if exists price_seq;

drop sequence if exists user_seq;

