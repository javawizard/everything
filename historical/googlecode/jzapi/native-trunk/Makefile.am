lib_LTLIBRARIES = testplugin.la

AM_CPPFLAGS = @CONF_CPPFLAGS@
AM_CXXFLAGS = @CONF_CXXFLAGS@

testplugin_la_SOURCES = testplugin.cpp
testplugin_la_LDFLAGS = -module -avoid-version
testplugin_la_CXXFLAGS= -I$(top_srcdir)/plugin_utils
testplugin_la_LIBADD  = $(top_builddir)/plugin_utils/libplugin_utils.la

EXTRA_DIST = \
	README.txt \
	testplugin.def \
	testplugin.sln \
	testplugin.vcproj

MAINTAINERCLEANFILES =	\
	Makefile.in
