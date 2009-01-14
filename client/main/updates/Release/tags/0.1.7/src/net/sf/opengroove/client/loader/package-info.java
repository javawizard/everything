/**
 * This package contains classes related to stuff that happens before OpenGroove is actually up and running. Stuff within this package must not reference stuff in other OpenGroove client packages, since this package's classes are typically called before OpenGroove has checked for updates, and so any classes loaded before then will not be running the updated versions when OpenGroove starts.
 */
package net.sf.opengroove.client.loader;