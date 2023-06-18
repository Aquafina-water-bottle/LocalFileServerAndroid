package com.rayrtheii.localfileserver

import android.content.Context
import fi.iki.elonen.router.RouterNanoHTTPD

class Router(port: Int, private val context: Context) : RouterNanoHTTPD(port) {
    init {
        addMappings()
        start(SOCKET_READ_TIMEOUT, false)
    }

    override fun addMappings() {
        addRoute("/(.)+", LocalAudioRouteHandler::class.java, context)
    }
}