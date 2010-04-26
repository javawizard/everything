import operations


class OperationList(object):
    """
    A collection of operations to be applied. Unlike Changeset, an operation list
    is not bound to a particular path. Changeset objects internally hold a
    reference to an OperationList instance and delegate a lot of their method
    to that instance.
    """
    def __init__(self, db):
        self.operations = []
        self.db = db
        self.metadata = {}
    
    def add(self, operation):
        """
        Adds a new operation to the end of this operation list.
        """
        self.operations.append(operation)
    
    append = add
    
    def apply(self):
        """
        Applies this operation list. This just delegates to the database right now.
        """
        self.db.apply(self)
    
    def __repr__(self):
        return "<OperationList of " + repr(self.operations) + ">"


class Changeset(object):
    """
    This class is the main class used to modify the database. TODO: document this more
    """
    def __init__(self, operation_list, path):
        object.__setattr__(self, "operation_list", operation_list)
        object.__setattr__(self, "path", path)
        parent_path, _, id = path.rpartition("/")
        object.__setattr__(self, "parent_path", parent_path)
        object.__setattr__(self, "id", id)
        object.__setattr__(self, "db", operation_list.db)
        object.__setattr__(self, "metadata", operation_list.metadata)
    
    def insert(self, db_type, **attributes):
        """
        Creates an insert operation that would create this object in the database.
        db_type is the type for the object. All other keyword arguments are
        attributes that will be initially set on the object if it does not exist
        when the insert is executed.
        """
        insert = operations.Insert.create(self.parent_path, self.id, db_type, **attributes)
        self.operation_list.add(insert)
        return self
    
    def delete(self):
        """
        Creates a delete operation that would delete this object from the database.
        """
        self.operation_list.add(operations.Delete.create(self.path))
        return self
    
    def set(self, **attributes):
        """
        Creates one set operation for each of the specified attributes. The operations
        will set the specified attributes to have the specified values. Unlike the
        update method, this method will create attributes if they don't exist.
        """
        for k, v in attributes.items():
            self.operation_list.add(operations.Set.create(self.path, k, v))
        return self
    
    def update(self, **attributes):
        """
        Creates one update operation for each of the specified attributes. The operations
        will set the specified attributes to have the specified values. Each attribute
        will only be set to the specified value if that attribute already existed on
        this object, though, which is the primary difference between this method and the
        set method.
        """
        for k, v, in attributes.items():
            self.operation_list.add(operations.Update.create(self.path, k, v))
        return self
    
    def unset(self, *attributes):
        """
        Creates one unset operation for each of the specified attribute names. The
        operations will unset the specified attributes on this object.
        """
        for k in attributes:
            self.operation_list.add(operations.Unset.create(self.path, k))
        return self
    
    __delattr__ = unset
    
    def __getitem__(self, item):
        # Enforcing that only strings can be used here, as passing an integer or a
        # slice won't cause errors until later on which might be hard to track down
        if not isinstance(item, basestring):
            raise TypeError("Changesets can only be obtained for string paths, not "
                            + "objects of type " + str(type(item)))
        if(item.startswith("/") or item == ""):
            path = item
        else:
            path = self.path + "/" + item
        return Changeset(self.operation_list, path)
    
    __div__ = __getitem__ # Changeset/'some'/'path'
    
    def apply(self):
        """
        Applies this changeset.
        
        This method is notable in not returning this changeset as its return value.
        The reasoning behind this is that once a changeset has been applied, the
        changeset is essentially useless, so there would be no point in returning
        it from this method. If there ends up being an application using OGDB that
        has a use for this method returning this changeset, the OG developers would
        be happy to change this method.
        """
        self.operation_list.apply()
    
    def __setattr__(self, name, value):
        self.set(**{name: value})
    
    def __getattr__(self, name):
        if name == "parent":
            return Changeset(self.operation_list, self.parent_path)
        raise AttributeError("No such attribute \"" + name + "\"")
    
    def __repr__(self):
        return ("<Changeset for path \"" + self.path + "\" of " 
                + repr(self.operation_list) + ">")
    
    def __eq__(self, other):
        return (isinstance(other, Changeset) and self.path == other.path
                and self.db is other.db)

























