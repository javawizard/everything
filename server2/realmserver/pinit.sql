create table webusers (
username varchar(64),
role varchar(64),
password varchar(200)
);

create table configuration (
name varchar(64),
value varchar(1024)
);

create table users (
username varchar(64),
password varchar(200)
);

create table usersettings (
username varchar(64),
name varchar(64),
value varchar(2048)
);

create table computerusersettings (
username varchar(64),
computername varchar(64),
name varchar(64),
value varchar(64)
);

create table computers (
username varchar(64),
computername varchar(64)
);
