"""
This module contains classes and functions relating to connecting to and using
remote services.
"""

from Queue import Queue, Empty
from autobus2 import net, messaging, exceptions
from utils import Suppress
from threading import RLock
import json
import autobus2


class Connection(object):
    """
    A connection to a remote service. This class allows for calling functions
    on the remote service, listening for events, and watching objects.
    
    Instances of this class should not be created directly; instead, a new Bus
    object should be created, and its connect function called.
    """
    def __init__(self, bus, socket, service_id):
        """
        Creates a new connection, given the specified parent bus and socket.
        This constructor sets up everything and then sends an initial message
        to the remote socket indicating what service is to be connected to.
        """
        self.bus = bus
        self.socket = socket
        self.service_id = service_id
        self.queue = Queue()
        self.query_map = {}
        self.query_lock = RLock()
        self.is_connected = True
        net.OutputThread(socket, self.queue.get).start()
        net.InputThread(socket, self.received, self.cleanup).start()
        # We query here so that an invalid service id will cause an exception
        # to be raised while constructing the service
        self.query(messaging.create_command("bind", False, service=service_id), timeout=10)

    def close(self):
        self.queue.put(None)
        net.shutdown(self.socket)
    
    def cleanup(self):
        with self.query_lock:
            self.socket.close()
            self.queue.put(None)
            self.is_connected = False
            for f in self.query_map.copy().itervalues():
                f(None)
    
    def send(self, message):
        if message:
            self.queue.put(message)
    
    def send_async(self, message, callback):
        """
        Sends the specified message. The specified callback will be called with
        the response.
        
        Unless the command isn't responded to by the remote side of the
        connection, the specified callback is guaranteed to be called. If the
        connection is not currently connected, a NotConnectedException will be
        raised. If the connection disconnects while waiting for a response, the
        callback will be called, passing in an instance of
        ConnectionLostException. If some other error occurs while processing,
        a suitable exception will be created and passed into the callback.
        
        The specified function will be called on the input thread for this
        connection, so it must not block for a significant amount of time; if
        it does, it will freeze up receiving of messages for this connection.
        """
        if not message:
            raise exceptions.NullMessageException
        with self.query_lock:
            if not self.is_connected:
                raise exceptions.NotConnectedException
            def wrapper(response):
                if not response:
                    callback(exceptions.ConnectionLostException())
                if response.get("_error", None):
                    callback(exceptions.CommandErrorException(response["_error"]["text"]))
                else:
                    callback(response)
            self.query_map[message["_id"]] = wrapper
        self.send(message)
    
    def query(self, message, timeout=30):
        if message is None:
            raise exceptions.NullMessageException
        q = Queue()
        with self.query_lock:
            if not self.is_connected:
                raise exceptions.NotConnectedException
            self.query_map[message["_id"]] = q.put
        self.send(message)
        try:
            response = q.get(timeout=timeout)
            with Suppress(KeyError):
                with self.query_lock:
                    del self.query_map[message["_id"]]
            if response is None: # Connection lost while waiting for a response
                raise exceptions.ConnectionLostException()
            if response.get("_error", None):
                raise exceptions.CommandErrorException(response["_error"]["text"])
            return response
        except Empty: # Timeout while waiting for response
            with Suppress(KeyError):
                with self.query_lock:
                    del self.query_map[message["_id"]]
            raise exceptions.TimeoutException()
    
    def received(self, message):
        if message["_type"] == 2: # response
            f = self.query_map.get(message["_id"], None)
            if f:
                f(message)
        if message["_type"] in [1, 3]:
            command = message["_command"]
            # TODO: add things for processing change and fire commands
            print "Invalid message received and ignored. Command: %s" % command
    
    def __getitem__(self, name):
        return Function(self, name)
    
    def __enter__(self):
        return self
    
    def __exit__(self, *args):
        # Might want to make __enter__ and __exit__ reentrant by using a
        # counter to track how many nested times we're checking the connection
        self.close()


class Function(object):
    """
    An object representing a remote function. Instances of this class are
    callable; calling them will invoke the remote function.
    
    Instances of this class can be obtained by doing
    some_connection["function_name"].
    """
    def __init__(self, connection, name):
        self.connection = connection
        self.name = name
    
    def __call__(self, *args, **kwargs):
        """
        Calls this function. The positional arguments passed to this call are
        passed to the function on the remote side when it is invoked. They must
        be JSON-encodable values; an InvalidValueException will be thrown if
        one of them is not.
        
        Two keyword arguments can be passed when calling a function:
        
        callback: This is autobus2.SYNC to call the function synchronously
        (which is what most people expect; the call will block until the remote
        service sends back a response for the function call, at which point it
        will be returned), None to call the function but return immediately
        without waiting for a response (the return value will be None), or a
        one-argument function (or other Python callable), which will cause this
        call to return immediately and the specified function to be invoked
        with the response once it arrives. The default, if callback is not
        specified, is SYNC.
        
        timeout: This only has any effect if SYNC is used as the callback (or
        the callback is not specified, since it defaults to SYNC). If the
        remote service has not sent a response in this many seconds, the call
        will stop immediately and throw a TimeoutException. The default, if
        timeout is not specified, is 30.
        
        If callback is set to a function and the connection is disconnected
        before the response is received, the callback will be called with None
        as its argument.
        
        If a callback is used and an exception happens while processing (or if
        the remote function throws an exception), the exception object itself
        is passed into the callback. If a callback is not used, the exception
        will be raised instead.
        """
        for a in args:
            try:
                json.dumps(a)
            except:
                raise exceptions.InvalidValueException
        callback = kwargs.get("callback", autobus2.SYNC)
        timeout = kwargs.get("timeout", 30)
        command = messaging.create_command("call", name=self.name, args=list(args))
        if callback is autobus2.SYNC:
            result = self.connection.query(command, timeout)
            if result.get("exception"):
                raise exceptions.RemoteUserException(result["exception"]["text"])
            return result["result"]
        elif callback is None:
            command["_type"] = 3 # Change to a notice
            self.connection.send(command)
        else:
            def wrapper(response):
                if isinstance(response, dict): # Normal response
                    if response.get("exception"): # Remote function threw an exception
                        callback(exceptions.RemoteUserException(result["exception"]["text"]))
                    else: # Remote function returned normally
                        callback(response["result"])
                else: # Some other exception while processing
                    callback(response)
            self.connection.send_async(command, wrapper)
            




































