import os
import sqlite3
import threading
import changeset as changeset_module
import query as query_module
from opengroove.utils import no_exceptions


class DB(object):
    """
    A connection to an OpenGroove Database. OpenGroove Database is a versioned
    object database. More info will come soon.
    """
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
                                     isolation_level=None, cached_statements=10)
        with no_exceptions:
            self.sqldb.execute("create table objects(id,path,parent)")
            self.sqldb.execute("create table attributes(path,name,value)")
        self.root = self[""]
        self.pre_apply = []
        self.post_apply = []
    
    def close(self):
        """
        Ensures the database has been closed.
        """
        with self.lock: # Lock the database, so no queries are performed during
                        # the close...
            self.sqldb.close()
    
    __del__ = close
    
    def __getitem__(self, path):
        # Make sure that we're looking at a string, not a slice or an int. Not
        # checking this now can cause some interesting problems later on.
        with self.lock:
            if not isinstance(path, basestring): 
                raise TypeError("Objects can only be queried by path name")
            if path == "":
                results = "", ""
            else:
                results = self.sqldb.execute("select id, parent from objects where "
                                             + "path = ?", [path]).fetchone()
                if results is None:
                    return None
            id, parent = results
            return DataObject(self, id, path, parent)
    
    __div__ = __getitem__ # database/'some'/'path'
    
    def apply(self, operation_list):
        """
        Applies the specified operation list. In general, you won't need to
        use this method; you'll usually call the apply() method on a changeset
        object, which ends up calling this method for you.
        """
        with self.lock:
            for listener in self.pre_apply:
                listener(operation_list)
            # FIXME: change this to store changes to version history
            for operation in operation_list.operations:
                operation.apply(self.sqldb, True)
            for listener in self.post_apply:
                listener(operation_list)
    
    def query(self):
        """
        Creates and returns a new, blank query that can be used to query
        this database. Typically, instead of using this method, you'll
        want to call db_query() or db_query_all() on an object, as those
        method will ensure that the query only queries that particular
        object and its children.
        """
        return query_module.Query(self)


def open_database(path):
    """
    Opens the OpenGroove Database located at the specified path, which
    should be a folder. If no such path exists, it will be created and
    made into a new, blank database. The newly-opened database is then
    returned.
    """
    return DB(path)


class DataObject(object):
    """
    An object obtained from the database. Applications shouldn't create
    instances of this class directly; only the OpenGroove DB itself
    should create instances of this class. If you need to get an object
    from the database, you can either run a query (by calling
    db.root.db_query(), db.query(), or some_other_db_object.db_query()),
    retrieve the object by path (for example, database["/some/object"]),
    or just use the root database object (db.root).
    
    Note that the root object cannot have attributes set on it. You
    should use other objects for setting attributes.
    """
    def __init__(self, db, id, path, parent_path):
        self.db = db
        self.db_id = id
        self.db_path = path
        self.db_parent_path = parent_path
        self.db_reload()
    
    def __getattr__(self, name):
        if name == "db_parent":
            return self.db[self.db_parent_path]
        value = self.attributes.get(name)
        if not value is None:
            return value
        raise AttributeError("No such attribute: " + name)
    
    def __getitem__(self, path):
        """
        Gets the object with the specified path, which can be either relative
        to this object (IE not starting with a forward slash) or absolute. If
        it is relative, it is interpreted as a child or descendant of this
        object. If it is absolute, it is interpreted as an absolute path in
        the database.
        """
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
        """
        Creates a new, blank changeset. This is how you actually modify the
        database; you create a new changeset, call various methods on it to
        modify it as needed, and then call its apply method.
        """
        operation_list = changeset_module.OperationList(self.db)
        changeset = changeset_module.Changeset(operation_list, self.db_path)
        return changeset
    
    def __repr__(self):
        return ("<DataObject for path \"" + self.db_path + "\" with attributes "
                + repr(self.attributes) + ">")
    
    def db_query(self):
        """
        Returns a newly-created query, blank except that it will only select
        objects that are immediate children of this object. The query can be
        further filtered as needed.
        """
        query = self.db.query()
        query.parent(self.db_path)
        return query
    
    def db_query_all(self):
        """
        Returns a newly-created query, blank except that it will only select
        objects that are descendants of this object. The query can be further
        filtered as needed.
        """
        query = self.db.query()
        query.ancestor(self.db_path)
        return query



























