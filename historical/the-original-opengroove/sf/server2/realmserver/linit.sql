create table $$prefix$$messages (
id varchar(128),
sender varchar(64),
computer varchar(64),
sent boolean
);

create table $$prefix$$messagerecipients (
id varchar(128),
recipient varchar(64),
computer varchar(64)
);