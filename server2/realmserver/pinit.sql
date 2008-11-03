create table $$prefix$$webusers (
username varchar(64),
role varchar(64),
password varchar(200)
);

create table $$prefix$$configuration (
name varchar(64),
value varchar(16384)
);

create table $$prefix$$users (
username varchar(64),
password varchar(200),
publiclylisted boolean
);

create table $$prefix$$userquotas (
username varchar(64),
name varchar(32),
value int
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
type varchar(32),
capabilities varchar(512),
lastonline bigint
);

create table $$prefix$$subscriptions (
type varchar(64),
username varchar(64),
onusername varchar(64),
oncomputername varchar(64),
onsettingname varchar(128),
deletewithtarget boolean,
properties varchar(1024)
);

create table $$prefix$$imessagequotas (
username varchar(64),
onusername varchar(64),
messagecount int
);

create table $$prefix$$help (
pathname varchar(512),
contents varchar(64511)
);

create table $$prefix$$predefinednotifications (
name varchar(128),
type varchar(128),
subject varchar(512),
message varchar(4096),
notes varchar(8192),
warnings varchar(1024)
);
