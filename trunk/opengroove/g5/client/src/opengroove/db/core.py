import os
import sqlite3
import threading
import changeset as changeset_module
from opengroove.utils import no_exceptions


class DB(object):
    def __init__(self, storage_path):
        """
        Initializes this database to read from and write to the specified
        path. The specified path will be created as a folder if it does
        not already exist.
        """
        self.lock = threading.RLock()
        sqlite_folder = os.sep.join([storage_path, "datastore-sqlite"])
        sqlite_path = os.sep.join([storage_path, "db"])
        if not os.path.exists(sqlite_folder):
            os.makedirs(sqlite_folder)
        self.sqldb = sqlite3.connect(sqlite_path, check_same_thread=False,
                                     isolation_level=None)
        with no_exceptions:
            self.sqldb.execute("create table objects(id,path,parent,type)")
        with no_exceptions:
            self.sqldb.execute("create table attributes(path,name,value)")
        self.root = self[""]
        self.pre_apply = []
        self.post_apply = []
    
    def __getitem__(self, path):
        # Make sure that we're looking at a string, not a slice or an int. Not
        # checking this now can cause some interesting problems later on.
        with self.lock:
            if not isinstance(path, basestring):
                raise TypeError("Objects can only be queried by path name")
            if path == "":
                results = "", "", ""
            else:
                results = self.sqldb.execute("select id, parent, type from objects where "
                                             + "path = ?", [path]).fetchone()
                if results is None:
                    return None
            id, parent, type = results
            return DataObject(self, id, path, parent, type)
    
    __div__ = __getitem__ # database/'some'/'path'
    
    def apply(self, operation_list):
        with self.lock:
            for listener in self.pre_apply:
                listener(operation_list)
            # FIXME: change this to store changes to version history
            for operation in operation_list.operations:
                operation.apply(self.sqldb, True)
            for listener in self.post_apply:
                listener(operation_list)


def open_database(path):
    """
    Opens the OpenGroove Database located at the specified path, which
    should be a folder. If no such path exists, it will be created and
    made into a new, blank database. The newly-opened database is then
    returned.
    """
    return DB(path)


class DataObject(object):
    def __init__(self, db, id, path, parent_path, type):
        self.db = db
        self.db_id = id
        self.db_path = path
        self.db_parent_path = parent_path
        self.db_type = type
        self.db_reload()
    
    def __getattr__(self, name):
        if name == "db_parent":
            return self.db[self.db_parent_path]
        value = self.attributes.get(name)
        if not value is None:
            return value
        raise AttributeError("No such attribute: " + name)
    
    def __getitem__(self, path):
        if not isinstance(path, basestring):
            raise TypeError("Objects can only be queried by path name")
        if path.startswith("/"):
            return self.db[path]
        return self.db[self.db_path + "/" + path]
    
    __div__ = __getitem__ # database/'some'/'path'
    
    def db_reload(self):
        """
        Reloads all of the attributes on this database object. If the object
        no longer exists in the database, all of its attributes will be removed.
        """
        with self.db.lock:
            self.attributes = dict((k, v) for k, v in self.db.sqldb.execute(
                               "select name, value from attributes where path = ?",
                               [self.db_path]))
    
    def db_changeset(self):
        operation_list = changeset_module.OperationList(self.db)
        changeset = changeset_module.Changeset(operation_list, self.db_path)
        return changeset
    
    def __repr__(self):
        return ("<DataObject for path \"" + self.db_path + "\" with attributes "
                + repr(self.attributes) + ">")




























