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
    username varchar(64),  -- The username of this user. This can't be changed for now.
    password varchar(512), -- The hexcoded SHA-512 hash of the user's password. Yes, I know that only 128 characters are needed to hold this.
    role     int           -- The role that has been assigned to this user
);
-- Holds the list of bzflag callsign groups and the role associated with them.
create table authgroups (
    name varchar(64), -- The name of the bzflag group. For example, BZTRAINING.ADMIN or DEVELOPERS.
    role int          -- The name of the role assigned to this bzflag group
);
-- Holds the list of bzflag callsigns and the role associated with them.
create table callsigns (
    callsign varchar(64), -- The callsign that this is for. For example, javawizard2539, Bambino, or MrDudle.
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
    target     int          -- The target that the permission applies to, either a group id, a server id, or -1 for global
);
-- Holds the list of groups on the administration site. The banfile and groupdb are stored in files, not in the database.
create table groups (
    groupid int,           -- The id of the group
    name    varchar(64)   -- The name of the group
);
-- Holds the list of servers on the administration site. The server's map, groupdb, and config are stored in actual files, not in here.
-- Whether the server is public, however, is stored here instead of in the config.
create table servers (
    serverid        int,           -- The id of the server
    name            varchar(64),   -- The name of the server, which is used as the server's public name if the server is public
    groupid         int,           -- The id of the server's parent group
    listed          boolean,       -- True if this server is public, false if it is not. If it is public, the public name is the name column.
    running         boolean,       -- True if this server is running, false if it is not. This is used to allow a server to remain shut down across bznetwork restart.
    dirty           boolean,       -- True if this server is dirty, false if it is not. A dirty server is one that has had changes made to its configuration that is currently running, and that has not been restarted since the changes were made. A dirty server is, in essence, one that is running on an old configuration and needs a restart for new configuration changes to be applied. When a server is started or shut down, the dirty flag is cleared, and it is never set when the server is not running.
    notes           varchar(4096), -- Some notes on the server. This can be any text, and serves simply to note what the server is for.
    inheritgroupdb  boolean        -- True if the server should inherit its parent group's groupdb. The parent group's groupdb always comes before the server's own groupdb when handing the groupdb to the server, so the server's groupdb can use bzflag groups defined in the parent group's groupdb.
);
-- Holds the actions performed on the server. This is simply a log that users with appropriate permissions can use to view what other users have done on the server. This is similar to other services (such as SourceForge)'s concept of auditing.
create table actions (
    provider varchar(64),    -- The name of the authentication provider that the user that triggered this event is using
    username varchar(64),    -- The username, which is specific to the authentication provider specified, of the user that caused this event
    when     timestamp,      -- The time at which the event occured 
    event    varchar(64),    -- The event itself. This is the name of the action that occured, and does not include any other details of the action. This should also not include any spaces. For example, "change-auth-provider", "change-server-groupdb", "create-internal-user", "delete-internal-user", and so on. When the action log for a user is deleted (by someone with such permissions), then their log is cleared, and a "action-log-cleared" event is added for that user.
    details  varchar(8192),  -- Additional details as to what the event is about. For example, the event "create-internal-user" might use this to specify information about the user that was created. Or "change-auth-provider" might specify what change actually happened (enabling a provider, disabling it, or changing the default provider).
    target   int             -- If this event had to do with a particular server or group, then this is its id. If not, this is -1 (including events on other objects that have ids, such as roles; -1 is used in that case also, although I might change that in the future)
);
-- Holds general server configuration. Configuration that is likely to be changed often is stored here. There should only ever be one row in this table.
create table configuration (
    welcome     varchar(8192), -- A message that is shown on the welcome screen, right at the top, above the list of servers. It can contain html.
    sitename    varchar(64),   -- The name of the site. For example, BZTraining. This must not contain html.
    contact     varchar(512),  -- Some sort of contact information that could adequately replace <contact> in the sentence "Get in touch with <contact> if you have problems with our site". This could be an email address, a list of people, or whatever.
    executable  varchar(512),  -- The executable that should be run to start the bzflag server. This is, by default, "bzfs".
    menuleft    boolean,       -- See the menuLeft parameter to the MainScreen constructor
    currentname boolean        -- See the headerScreenName parameter to the MainScreen constructor
);



-- Now for some initial table rows.
insert into configuration values ('Congratulations! You''ve successfully installed BZNetwork onto your server. Head on over to the Configuration page to change this message. Then check out the Getting Started link on the Help page to get started.', 
'MySiteName', 'mybznetworksite@example.com', 'bzfs', true, false);
insert into idsequence values (10);
insert into roles values (1, 'Administrator');
insert into permissions values (1, 'all', -1);
-- The hashed password is admin, so both the username and the password are admin.
insert into users values ('admin', '-3852bb345289d5a25f5bad0617ab023e1f185ad5c7fea0dc0c154e27f46ce22b8d9cb20538e32cb143ca2e954804756f37e068aeec2938ac72396227216f8814', 1);
insert into actions values ('internal', 'admin', NOW(), 'setup', 'BZNetwork installation was successfully completed.', -1);































