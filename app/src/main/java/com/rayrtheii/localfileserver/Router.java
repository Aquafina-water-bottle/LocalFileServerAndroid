package com.rayrtheii.localfileserver;

import android.content.Context;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.io.IOException;

public class Router extends RouterNanoHTTPD {
    private final Context context;
    public static String contentType;

    public Router(Integer port, Context context) throws IOException {
        super(port);
        this.context = context;

        contentType = new ContentType("; charset=UTF-8").getContentTypeHeader();
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public void addMappings() {
        addRoute("/(.)+", LocalAudioRouteHandler.class, this.context);
    }
}
