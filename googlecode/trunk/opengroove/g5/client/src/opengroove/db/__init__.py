from core import open_database
connect = open_database

def browser(db):
    """
    Starts a graphical browser that can be used to browse the objects
    in the specified database. Arbitrary queries can be executed with
    the browser, and arbitrary changesets can as well. This function
    requires GTK+ and PyGTK to be installed to function correctly.
    
    The database supplied must be a database obtained from
    opengroove.db.open(). Specifically, it can't be just a string
    representing the path to the database.
    """
    # We're not doing the import until this function is called so that
    # OpenGroove DB can be used on systems that don't support GTK as
    # long as they don't call this function.
    from browser import browser as internal_browser
    internal_browser(db)
