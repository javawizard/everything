from traceback import print_exception

# Copied from
# http://code.activestate.com/recipes/52549-curry-associating-parameters-with-a-function/
class Curry(object):
    """
A class that can wrap another function and predetermine some arguments.

This class wraps any given function with a list of positional arguments and keyword 
arguments. When the instance of this class is called, the specified function is called,
with the predetermined positional arguments followed by those passed into the invocation
of the instance of this class, and all of the keyword arguments together.
""" 
    def __init__(self, function, *args, **kwargs):
        self.function = function
        self.pending = args[:]
        self.kwargs = kwargs.copy()

    def __call__(self, *args, **kwargs):
        if kwargs and self.kwargs:
            kw = self.kwargs.copy()
            kw.update(kwargs)
        else:
            kw = kwargs or self.kwargs

        return self.function(*(self.pending + args), **kw)


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




