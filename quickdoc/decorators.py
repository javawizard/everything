
from quickdoc import core as _core

def documented(thing):
    _core.documented(thing)
    return thing


def parameter(name, description, required=None):
    def _(thing):
        _core.parameter(thing, name, description, required)
        return thing
    return _


def changed(version, description):
    def _(thing):
        _core.changed(thing, version, description)
        return thing
    return _


def new(version, description):
    def _(thing):
        _core.new(thing, version, description)
        return thing
    return _


def since(version):
    def _(thing):
        _core.since(thing, version)
        return thing
    return _


param = parameter






