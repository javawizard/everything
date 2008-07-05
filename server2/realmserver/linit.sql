create table $$prefix$$storedmessages (
id varchar(128),
sender varchar(64),
recipient varchar(64),
fromcomputer varchar(64),
tocomputer varchar(64),
finalized boolean,
accessed boolean,
metadata varchar(1192)
);

create table $$prefix$$softdeletes (
id varchar(128),
computer varchar(64)
);

create table $$prefix$$storedmessagedata (
id varchar(128),
block int,
contents blob
);