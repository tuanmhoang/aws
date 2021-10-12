create table "user" (
  id bigserial not null,
  name varchar(50) not null,
  surname varchar(50) not null,
  birthday varchar(50) not null,
  primary key (id)
);

create table subscription (
  id bigserial not null,
  user_id bigint not null references "user" (id),
  start_date varchar(50) not null,
  primary key (id)
);