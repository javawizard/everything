import os.path
import os
import sqlite3
import threading


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
        os.makedirs(sqlite_folder)
        self.sqldb = sqlite3.connect(sqlite_path, check_same_thread=False, 
                                     isolation_level=None)
        self.root = self[""]
    
    def __getitem__(self, path):
        # Make sure that we're looking at a string, not a slice or an int. Not
        # checking this now can cause some interesting problems later on.
        with self.lock:
            if not isinstance(path, str):
                raise TypeError("Objects can only be queried by path name")
            if path == "":
                results = "", "", ""
            else:
                results = self.sqldb.execute("select id, parent, type from objects where "
                                             + "path = ?", path).fetchone()
                if results is None:
                    return None
            id, parent, type = results
            return DataObject(self, id, path, parent, type)


def open_database():
    print "Opening db"


class DataObject(object):
    def __init__(self, db, id, path, parent_path, type):
        self.db = db
        self.db_id = id
        self.db_path = path
        self.db_parent_path = parent_path
        self.db_type = type
    
    def __getattr__(self, name):
        if name == "db_parent":
            return self.db[self.db_parent_path]
        raise AttributeError("No such attribute: " + name)
    
    def __getitem__(self, path):
        if not isinstance(path, str):
            raise TypeError("Objects can only be queried by path name")
        if path.startswith("/"):
            return self.db[path]
        return self.db[self.path + "/" + path]



























