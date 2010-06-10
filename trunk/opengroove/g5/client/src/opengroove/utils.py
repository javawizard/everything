from traceback import print_exception


class BlankObject(object):
    """
    A blank class.
    
    A class that has no attributes but has a __dict__. This allows instances of the
    class to be assigned arbitrary attributes.
    """
    pass


class NoExceptions:
    """
    A "with" statement context manager for silently suppressing exceptions.
    
    A class and a singleton object that can be used as the context manager of the "with"
    statement to cause it to silently suppress all exceptions that occur within it.
    """
    def __enter__(self):
        pass
    
    def __exit__(self, type, value, traceback):
        return True

no_exceptions = NoExceptions()


class ExceptionWarning:
    """
    A "with" statment context manager for warning about exceptions and then suppressing them.
    
    A class and a singleton object that can be used as the context manager of the "with"
    statement to cause it to print a warning to stderr whenever an exception occurs within
    the block and then suppress it.
    """
    def __enter__(self):
        pass
    
    def __exit__(self, type, value, traceback):
        print_exception(type, value, traceback)
        return True
    

exception_warning = ExceptionWarning()




