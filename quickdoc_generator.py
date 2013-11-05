
import argparse
from fileutils import File, YIELD, SKIP, RECURSE, create_temporary_folder
import importlib
import inspect
from singledispatch import singledispatch
import types
import pydoc
from collections import namedtuple
import sys
import subprocess

FILE = File(__file__)
MethodWrapper = namedtuple("MethodWrapper", ["function"])
PropertyWrapper = namedtuple("PropertyWrapper", ["name", "prop"])

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--discover", nargs="+", default=[])
    parser.add_argument("--modules", nargs="+", default=[])
    parser.add_argument("--rst", default=None)
    parser.add_argument("--html", default=None)
    parser.add_argument("--skip-failed", action="store_true")
    args = parser.parse_args()
    #output_dir = File(args.output)
    #output_dir.create_folder(ignore_existing=True, recursive=True)
    
    module_names = set()
    for folder_name in args.discover:
        folder = File(folder_name)
        # Add the folder to the path so we can import things from it
        sys.path.insert(0, folder.path)
        print "Discovering packages and modules in %r..." % folder.path
        for thing in folder.recurse(lambda f:
                YIELD if f.name.endswith(".py") and f.name != "__init__.py" and f != folder.child("setup.py") else 
                True if f.child("__init__.py").exists else
                RECURSE if f == folder else SKIP):
            name = thing.get_path(relative_to=folder, separator=".")
            # Strip off trailing ".py" for modules
            if thing.is_file:
                name = name[:-3]
            print "Discovered package/module %s" % name
            module_names.add(name)
    for name in args.modules:
        print "Including module %s on request" % name
        module_names.add(name)
    
    modules = set()
    for module_name in module_names:
        print "Importing %s" % module_name
        try:
            module = importlib.import_module(module_name)
        except:
            if args.skip_failed:
                print "IMPORT FAILED, ignoring."
            else:
                raise
        else:
            modules.add(module)
    
    modules = sorted(modules, key=lambda m: m.__name__)
    
    if args.rst:
        rst = File(args.rst)
    else:
        rst = create_temporary_folder(delete_on_exit=True)
    rst.create_folder(ignore_existing=True, recursive=True)
    module_dir = rst.child("modules")
    module_dir.create_folder(True, True)
    
    # conf = "project = {0!r}\n".format("Test Project")
    conf = ""
    rst.child("conf.py").write(conf)
    
    with rst.child("contents.rst").open("wb") as contents:
        contents.write(".. toctree::\n")
        for module in modules:
            contents.write("   modules/{0}.rst\n".format(module.__name__))
    
    for module in modules:
        with module_dir.child(module.__name__ + ".rst").open("wb") as m:
            display(module, m, 0)
    
    if args.html:
        html = File(args.html)
        with rst.as_working:
            subprocess.check_output(["sphinx-build", "-b", "html",
                                     rst.path, html.path])


def title(stream, level, text):
    character = "=-^%#&"[level]
    stream.write("\n\n" + character * len(text) + "\n" + text + "\n" + character * len(text) + "\n\n")


class IndentStream(object):
    def __init__(self, stream, indent="", initial=True):
        self.stream = stream
        self.indent = indent
        self.need_indent = initial
    
    def write(self, text):
        for char in text:
            if self.need_indent and char != "\n":
                self.need_indent = False
                self.stream.write(self.indent)
            self.stream.write(char)
            if char == "\n":
                self.need_indent = True


@singledispatch
def display(thing, stream, level):
    print "SKIPPING %r" % thing


@display.register(types.ModuleType)
def _(module, stream, level):
    synopsis, doc = pydoc.splitdoc(inspect.getdoc(module) or "")
    title(stream, level, ":mod:`" + module.__name__ + "` --- " + synopsis)
    stream.write(".. module:: " + module.__name__ + "\n   :synopsis: " + synopsis + "\n\n")
    stream.write(doc or "")
    # stream.write("\n\n.. contents:: Things\n   :depth: 1\n   :local:\n\n")
    for name in sorted(dir(module)):
        thing = getattr(module, name)
        if isinstance(thing, types.FunctionType):
            continue
        if should_document_module_member(name, thing, module):
            display(thing, stream, level + 1)
    function_names = [n for n in sorted(dir(module)) if isinstance(getattr(module, n), types.FunctionType) and should_document_module_member(n, getattr(module, n), module)]
    if function_names:
        title(stream, level + 1, "Functions")
        for name in function_names:
            thing = getattr(module, name)
            display(thing, stream, level + 2)


def should_document_module_member(name, thing, module):
    if not pydoc.visiblename(name, getattr(module, "__all__", None), thing):
        return False
    if hasattr(module, "__all__"):
        return True
    if isinstance(thing, types.ModuleType):
        return False
    if hasattr(thing, "__module__") and thing.__module__ != module.__name__:
        return False
    # Skip aliases for now. Might want to document aliases specially in the
    # future.
    if hasattr(thing, "__name__") and thing.__name__ != name:
        return False
    return True


@display.register(type)
def _(cls, stream, level): #@DuplicatedSignature
    print "Class {}".format(cls.__name__)
    title(stream, level, "Class " + cls.__name__)
    stream.write(inspect.getdoc(cls) or "")
    if cls.__init__ is object.__init__:
        spec = ""
    else:
        try:
            inspect.getargspec(cls.__init__)
        except:
            print "Skipping"
            spec = ""
        else:
            spec = inspect.formatargspec(*inspect.getargspec(cls.__init__))
    stream.write("\n\n.. class:: " + cls.__name__ + spec + "\n\n")
    class_stream = IndentStream(stream, "   ")
    class_stream.write(inspect.getdoc(cls.__init__) or "")
    for name, kind, definer, thing in sorted(inspect.classify_class_attrs(cls)):
        print "Class member {}".format(name)
        thing = getattr(cls, name)
        if definer is cls and pydoc.visiblename(name, None, thing):
            # TODO: Handle nested classes here
            if callable(thing):
                try:
                    inspect.getargspec(thing)
                except:
                    print "Skipping"
                else:
                    display(MethodWrapper(thing), class_stream, level + 1)
            elif isinstance(thing, property):
                display(PropertyWrapper(name, thing), class_stream, level + 1)
            else:
                display(thing, class_stream, level + 1)


@display.register(MethodWrapper)
def _(wrapper, stream, level): #@DuplicatedSignature
    function = wrapper.function
    stream.write("\n\n.. method:: " + function.__name__ + inspect.formatargspec(*inspect.getargspec(function)) + "\n\n")
    method_stream = IndentStream(stream, "   ")
    method_stream.write(inspect.getdoc(function) or "")


@display.register(PropertyWrapper)
def _(prop, stream, level): #@DuplicatedSignature
    stream.write("\n\n.. attribute:: " + prop.name + "\n\n")
    prop_stream = IndentStream(stream, "   ")
    prop_stream.write(inspect.getdoc(prop.prop) or "")


@display.register(types.FunctionType)
def _(function, stream, level): #@DuplicatedSignature
    stream.write("\n\n.. function:: " + function.__name__ + inspect.formatargspec(*inspect.getargspec(function)) + "\n\n")
    function_stream = IndentStream(stream, "   ")
    function_stream.write(inspect.getdoc(function) or "")


if __name__ == "__main__":
    main()



















