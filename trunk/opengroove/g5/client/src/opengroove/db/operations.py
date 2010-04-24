

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
        # We're good to go with the insert.
        


























