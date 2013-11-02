
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