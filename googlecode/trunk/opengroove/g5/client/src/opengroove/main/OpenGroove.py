import wx


def start():
    print "Hello from what will be OpenGroove!"
    app = wx.App(False)
    frame = wx.Frame(None, wx.ID_ANY, "Hello World")
    frame.Show(True)
    app.MainLoop()
