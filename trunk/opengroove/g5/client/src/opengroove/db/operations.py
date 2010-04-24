

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


class InsertOperation:
    
    @staticmethod
    def create(db_parent, db_id, db_type, **attributes):
        """
        Creates and returns a new insert operation. db_parent is the path of the
        parent object to use. db_id is the id for this new object. db_type is the
        type of this new object. (All of those are strings.)
        
        All remaining keyword arguments are taken to be the initial attributes
        that the insert should create on the object when executed.
        """
        op = InsertOperation()
        op.parent = db_parent
        op.id = db_id
        op.path = db_parent + "/" + db_id
        op.type = db_type
        op.attributes = attributes.copy()
    
    @staticmethod
    def decode(text):
        raise Exception("Not implemented yet")
    
    def encode(self):
        raise Exception("Not implemented yet")
    
    def apply(self, db, invert):
        # First we check to see if the object is already in the db
        if db.execute("select path from objects where path = ?", self.path
                      ).fetchone() != None:
            # An object with this id and parent already exists.
            return [] if invert else None
        if db.execute("select path from objects where path = ?", self.parent
                      ).fetchone() == None:
            # The parent object doesn't exist.
            return [] if invert else None
        # We're good to go with the insert. First, we'll add the object.
        db.execute("insert into objects values (?,?,?,?)", 
                   self.id, self.path, self.parent, self.type)
        # Now we'll add all of the object's attributes.
        for attribute, value in self.attributes.items():
            db.execute("insert into attributes values (?,?,?)", 
                       self.path, attribute, value)
        # We're done! Now we just figure out the inverse if needed, and that's it.
        if invert:
            return [DeleteOperation.create(self.path)]


class DeleteOperation:
    pass

























