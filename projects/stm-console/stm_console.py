
import remote_console
import stm
import stm.datatypes
import code as _code_module
import sys

class STMConsole(remote_console.RemoteConsole):
    traceback_strip = 1
    
    def __init__(self, *args, **kwargs):
        remote_console.RemoteConsole.__init__(self, *args, **kwargs)
        # Preload the console's locals with the core STM functions
        for name in stm.__all__:
            value = getattr(stm, name)
            @stm.atomically
            def _():
                if name not in self.locals:
                    self.locals[name] = value
    
    # We override runsource with merged copies of runsource and runcode from
    # code.InteractiveInterpreter, with modifications to display the result of
    # an expression outside of the context of the transaction in which it was
    # run
    def runsource(self, source, filename="<input>", symbol="single"):
        if source.strip() == "":
            return False
        
        try:
            code = self.compile(source, filename, symbol)
        except (OverflowError, SyntaxError, ValueError):
            self.showsyntaxerror(filename)
            return False
        if code is None:
            return True
        # See if it's a valid expression
        try:
            self.compile(source, filename, "eval")
            is_expression = True
        except:
            is_expression = False
        try:
            @stm.atomically
            def result():
                if is_expression:
                    return eval(source, globals(), self.locals)
                else:
                    exec code in globals(), self.locals
                    return None
            if result is not None:
                print result
        except SystemExit:
            raise
        except:
            self.showtraceback()
        else:
            if _code_module.softspace(sys.stdout, 0):
                print

        return False


class STMConsoleServer(remote_console.RemoteConsoleServer):
    def __init__(self, *args, **kwargs):
        remote_console.RemoteConsoleServer.__init__(self, *args, **kwargs)
        if not isinstance(self.local_vars, stm.datatypes.TDict):
            raise Exception("local_vars passed to STMConsoleServer must be a "
                            "TDict")
    
    def start_remote_console(self, connection):
        STMConsole(connection, self.local_vars).start()


def listen(port):
    STMConsoleServer(port, local_vars=stm.datatypes.TDict()).start()











