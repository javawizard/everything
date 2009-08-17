package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Server;

public class LogSearchModel implements Serializable
{
    private Server[] servers;
    private String[] events;
}
