create table $$prefix$$storedmessages (
id varchar(128),
sender varchar(64),
recipient varchar(64),
fromcomputer varchar(64),
tocomputer varchar(64),
maxchunks int,
maxchunksize int,
lifecycle int,
lifecycleprogress int,
lifecycletotal int,
needslifecycleupdate boolean,
finalized boolean,
approved boolean,
metadata varchar(4096)
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