package com.rayrtheii.localfileserver

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response.IStatus
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse
import fi.iki.elonen.router.RouterNanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource

class LocalAudioRouteHandler : RouterNanoHTTPD.DefaultHandler() {
    override fun getText(): String {
        return "not implemented"
    }

    override fun getMimeType(): String {
        return "text/html"
    }

    override fun getStatus(): IStatus {
        return NanoHTTPD.Response.Status.OK
    }

    override fun get(
        uriResource: UriResource,
        urlParams: Map<String, String>,
        session: IHTTPSession
    ): NanoHTTPD.Response {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", "gamer")
    }
}