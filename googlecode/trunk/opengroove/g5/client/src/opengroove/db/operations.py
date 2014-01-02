

"""
So, all of the operations have essentially the following methods:

apply(db, invert): Applies the changes represented by this operation to the
specified sqlite database representing the datastore. If invert is True, then 
a list of operations needed to undo this change is generated and returned. 

create(...): Static. The arguments are specific to the operation type, and can
be whatever would make sense. This returns a new instance of that operation
representing the arguments passed in.

decode(text): Static. Decodes the specified string into an instance of this class. 

encode(...): Encodes this instance into a string that can be passed to decode().


"""


class Insert(object):
    
    @staticmethod
    def create(db_parent, db_id, **attributes):
        """
        Creates and returns a new insert operation. db_parent is the path of the
        parent object to use. db_id is the id for this new object. (All of those
        are strings.)
        
        All remaining keyword arguments are taken to be the initial attributes
        that the insert should create on the object when executed.
        """
        op = Insert()
        op.parent = db_parent
        op.id = db_id
        op.path = db_parent + "/" + db_id
        op.attributes = attributes.copy()
        return op
    
    @staticmethod
    def decode(text):
        raise Exception("Not implemented yet")
    
    def encode(self):
        raise Exception("Not implemented yet")
    
    def apply(self, db, invert):
        # First we check to see if the object is already in the db
        if db.execute("select path from objects where path = ?", [self.path]
                      ).fetchone() is not None:
            # An object with this id and parent already exists.
            return [] if invert else None
        # Now we'll make sure the parent exists. If the parent is the empty
        # string, then the parent is the root object, which always exists.
        if not self.parent == "":
            if db.execute("select path from objects where path = ?", [self.parent]
                          ).fetchone() is None:
                # The parent object doesn't exist.
                return [] if invert else None
        # We're good to go with the insert. First, we'll add the object.
        db.execute("insert into objects values (?,?,?)",
                   [self.id, self.path, self.parent])
        # Now we'll add all of the object's attributes.
        for attribute, value in self.attributes.items():
            db.execute("insert into attributes values (?,?,?)",
                       [self.path, attribute, value])
        # We're done! Now we just figure out the inverse if needed, and that's it.
        if invert:
            return [Delete.create(self.path)]
    
    def __repr__(self):
        return ("<Insert path \"" + self.path + "\" with attributes " 
                + repr(self.attributes) + ">")


class Delete(object):
    @staticmethod
    def create(path):
        op = Delete()
        op.path = path
        return op
    
    @staticmethod
    def decode(text):
        pass
    
    def encode(self):
        pass
    
    def apply(self, db, invert):
        # First we make sure the object actually exists
        if db.execute("select path from objects where path = ?", [self.path]
                      ).fetchone is None:
            # This object doesn't exist anyway
            return [] if invert else None
        # Now that we know the object exists, we'll build its inverse
        # if requested.
        inverse = None
        if invert:
            inverse = []
            self.build_inverted_list(db, self.path, inverse)
        # Now we delete the object and its attributes
        db.execute("delete from objects where path = ?", [self.path])
        db.execute("delete from attributes where path = ?", [self.path])
        # The object's gone. Now we return the inverse, and we're done.
        return inverse
    
    def build_inverted_list(self, db, path, inverse):
        """
        Builds a list of insert operations that can be used to reconstruct the
        complete tree starting at path and recursively traveling through its
        children. The operations are populated into inverse.
        """
        # We'll get information on the object itself
        parent, id = db.execute("select parent, id from objects "
                                       + "where path = ?", [path]).fetchone()
        # Now we'll list the object's attributes
        attributes = {}
        for attribute, value in db.execute("select name, value from attributes "
                                            + "where path = ?", [path]):
            attributes[attribute] = value
        # Now we create the insert for this object
        insert = Insert.create(parent, id, **attributes);
        inverse.append(insert);
        # We've built the insert for this object. Now we'll get all child objects.
        for child in db.execute("select path from objects where parent = ?", [path]):
            self.build_inverted_list(db, child, inverse)
    
    def __repr__(self):
        return "<Delete path \"" + self.path + "\">"


class Set(object):
    @staticmethod
    def create(path, attribute, value):
        """
        Creates and returns a new insert operation. path is the path of the object
        to set an attribute on. attribute is the name of the attribute to set, and
        value is the value the attribute should be set to.
        """
        op = Set()
        op.path = path
        op.attribute = attribute
        op.value = value
        return op
    
    def apply(self, db, invert):
        # First we make sure the object exists
        if db.execute("select path from objects where path = ?", [self.path]
                      ).fetchone() is None:
            return [] if invert else None
        # The object exists. Now we check to see if the attribute already exists.
        if db.execute("select name from attributes where path = ? and name = ?",
                      [self.path, self.attribute]).fetchone() is None:
            # The attribute does not exist. We'll create it and return
            # an unset as the inverse.
            db.execute("insert into attributes values (?,?,?)", [self.path,
                       self.attribute, self.value])
            if(invert):
                return [Unset.create(self.path, self.attribute)]
        else:
            # The attribute does exist. Well update it and return
            # an update as the inverse.
            db.execute("update attributes set value = ? where path = ? and name = ?",
                       [self.value, self.path, self.attribute])
            if(invert):
                return [Update.create(self.path, self.attribute, self.value)]
    
    def __repr__(self):
        return ("<Set on path \"" + self.path + "\" attribute \"" + self.attribute
                + "\" to \"" + self.value + "\">")


class Update(object):
    @staticmethod
    def create(path, attribute, value):
        op = Update()
        op.path = path
        op.attribute = attribute
        op.value = value
        return op
    
    def apply(self, db, invert):
        # First we make sure the object exists
        if db.execute("select path from objects where path = ?", [self.path]
                      ).fetchone() is None:
            return [] if invert else None
        # The object exists. Now we check to see if the attribute already exists.
        if db.execute("select name from attributes where path = ? and name = ?",
                      [self.path, self.attribute]).fetchone() is None:
            # The attribute does not exist, which means we have nothing to do.
            return [] if invert else None
        else:
            # The attribute does exist. Well update it and return
            # an update as the inverse.
            db.execute("update attributes set value = ? where path = ? and name = ?",
                       [self.value, self.path, self.attribute])
            if(invert):
                return [Update.create(self.path, self.attribute, self.value)]
    
    def __repr__(self):
        return ("<Update on path \"" + self.path + "\" attribute \"" + self.attribute
                + "\" to \"" + self.value + "\">")


class Unset(object):
    @staticmethod
    def create(path, attribute):
        op = Unset()
        op.path = path
        op.attribute = attribute
        return op
    
    def apply(self, db, invert):
        # First we make sure the object exists
        if db.execute("select path from objects where path = ?", [self.path]
                      ).fetchone() is None:
            return [] if invert else None
        # The object exists. Now we check to see if the attribute exists.
        if db.execute("select name from attributes where path = ? and name = ?",
                      [self.path, self.attribute]).fetchone() is None:
            # The attribute does not exist, so we don't have anything to do.
            return [] if invert else None
        else:
            # The attribute does exist. We'll get its current value, delete it, and
            # return a set operation as the inverse.
            # TODO: this statment could really be merged with the statement above
            # it to save on number of queries that have to be run.
            if invert:
                old_value = db.execute("select value from attributes where path = ? and "
                                       + "name = ?", [self.path, self.name]).fetchone()[0]
            db.execute("delete from attributes where path = ? and name = ?",
                       [self.path, self.name])
            if invert:
                return [Set.create(self.path, self.attribute, old_value)]
    
    def __repr__(self):
        return "<Unset on path \"" + self.path + "\" attribute \"" + self.attribute + "\">"



























