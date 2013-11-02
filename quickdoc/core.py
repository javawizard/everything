
import inspect
from collections import namedtuple
from quickdoc.indentstream import IndentStream
from StringIO import StringIO

Parameter = namedtuple("Param", ["name", "description", "required"])
Changed = namedtuple("Changed", ["version", "description"])
New = namedtuple("New", ["version", "description"])
Since = namedtuple("Since", ["version"])


def get_info(thing):
    try:
        thing.__quickdoc__
    except AttributeError:
        thing.__quickdoc__ = {}
    thing.__quickdoc__.setdefault("records", [])
    return thing.__quickdoc__


def documented(thing):
    info = get_info(thing)
    
    if "docstring" in info:
        doc = info["docstring"]
    else:
        doc = inspect.getdoc(thing) or None
        info["docstring"] = doc
    
    doc_stream = StringIO()
    generate_docstring(thing, doc, doc_stream)
    thing.__doc__ = doc_stream.getvalue()


def get_parameters(thing):
    info = get_info(thing)
    argspec = inspect.getargspec(thing)
    # TODO: Split this out into its own function; it's quite useful by itself
    defaults = dict(zip(argspec.args[-len(argspec.defaults):], argspec.defaults))
    


def generate_docstring(thing, doc, stream):
    info = get_info(thing)
    stream.write(doc)
    
    generate_docstring_parameters("Required", [p for p in info["records"] if
                           isinstance(p, Parameter) and p.required], stream)
    generate_docstring_parameters("Optional", [p for p in info["records"] if
                           isinstance(p, Parameter) and not p.required], stream)
    
    changes = [c for c in info["records"] if isinstance(c, (Changed, New))]
    if changes:
        stream.write("\n\nChanges:")
        for change in changes:
            prefix = "Changed" if isinstance(change, Changed) else "New"
            stream.write("\n * ")
            change_stream = IndentStream(stream, "   ", False)
            change_stream.write(prefix + " in version " + change.version
                                + " - " + change.description)
    
    since = [s for s in info["records"] if isinstance(s, Since)]
    if since:
        stream.write("\n\nNew in version " + since[0].version + ".")


def generate_docstring_parameters(heading, parameters, stream):
    if parameters:
        stream.write("\n\n%s parameters:" % heading)
        for name, description, _ in parameters:
            stream.write("\n * ")
            param_stream = IndentStream(stream, "   ", False)
            param_stream.write(name + ": " + description)
    
    
    

def _make_record_inserter(cls, function_name):
    def insert_record(thing, *args):
        info = get_info(thing)
        record = cls(*args)
        info["records"].insert(0, record)
        documented(thing)
    insert_record.__name__ = function_name
    return insert_record


parameter = _make_record_inserter(Parameter, "parameter")
changed = _make_record_inserter(Changed, "changed")
new = _make_record_inserter(New, "new")
since = _make_record_inserter(Since, "since")







