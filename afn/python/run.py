#!/usr/bin/env python

import sys
sys.path.append("src")
if len(sys.argv) <= 1:
    print 'usage: run python_module_name'
    print 'adds "src" to the path, then imports the specified module and calls'
    print 'its main function with no arguments. The argument representing the'
    print 'module name will be removed from sys.argv first so that additional'
    print 'arguments can be provided to the module.'
    sys.exit(0)
module_name = sys.argv[1]
del sys.argv[1]
__import__(module_name).main()
