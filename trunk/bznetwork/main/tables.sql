-- Some tables have an integer identifier, whereas others use a string identifier. Integer identifiers are used where a string identifier
-- might be changed, and string identifiers are used when nothing else is dependent on the identifier and therefore changing it wouldn't 
-- have any external effect, or where the string identifier has to be a particular value from an external source (such as a bzflag group)
-- and so it wouldn't feasibly change while representing the same entity.

-- Holds the next sequence number. One row in this table.
create table idsequence (
    nextvalue int
);
-- Holds the list of bznetwork-internal users.
create table users (
    username varchar(64),  -- The username of this user
    password varchar(512), -- The hash of the user's password
    role     int           -- The role that has been assigned to this user
);
-- Holds the list of bzflag callsign groups and the role associated with them.
create table authgroups (
    name varchar(64), -- The name of the bzflag group
    role int          -- The name of the role assigned to this bzflag group
);
-- Holds the list of bzflag callsigns and the role associated with them.
create table callsigns (
    callsign varchar(64), -- The callsign that this is for
    role int              -- The role that is assigned to this callsign
);
-- Holds the list of roles on the server.
create table roles (
    roleid int,        -- The id of the role
    name   varchar(64) -- The name of the role
);
-- Holds the permissions granted to a particular role.
create table permissions (
    roleid     int,         -- The id of the role that this permission applies to
    permission varchar(64), -- The permission being applied
    target     int,         -- The target that the permission applies to, either a group id, a server id, or -1 for global
    parameter  varchar(64)  -- The parameter to this permission, if it is a permission that needs a parameter
);
-- Holds the list of groups on the administration site.
create table groups (
    groupid int,         --The id of the group
    name    varchar(64)  -- The name of the group
)
-- Holds the list of servers on the administration site. The server's map, groupdb, and config are stored in actual files, not in here.
-- Whether the server is public, however, is stored here instead of in the config.
create table servers (
    serverid int,         -- The id of the server
    name     varchar(64), -- The name of the server
    public   boolean,     -- True if this server is public, false if it is not. If it is public, the public name is the name column.
    running  boolean      -- True if this server is running, false if it is not. This is used to allow a server to remain shut down across bznetwork restart.
);





































