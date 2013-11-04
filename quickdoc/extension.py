
from docutils.parsers.rst import Directive

class QuickdocDirective(Directive):
    optional_argument = 1
    has_content = True
    
    def __init__(self, name, arguments, options, content, lineno, content_offset, block_text, state, state_machine):
        print "QUICKDOC WAS HERE"
        print "block_text: %r" % block_text
        print "content: %r" % content
        print "state: %r" % state
        print "type(state): %r" % type(state)
    
    def run(self):
        return []

def setup(sphinx):
    sphinx.add_directive("quickdoc", QuickdocDirective)


