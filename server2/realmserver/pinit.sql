create table $$prefix$$webusers (
username varchar(64),
role varchar(64),
password varchar(200)
);

create table $$prefix$$configuration (
name varchar(64),
value varchar(1024)
);

create table $$prefix$$users (
username varchar(64),
password varchar(200)
);

create table $$prefix$$usersettings (
username varchar(64),
name varchar(64),
value varchar(2048)
);

create table $$prefix$$computerusersettings (
username varchar(64),
computername varchar(64),
name varchar(64),
value varchar(2048)
);

create table $$prefix$$computers (
username varchar(64),
computername varchar(64),
type varchar(32)
);
