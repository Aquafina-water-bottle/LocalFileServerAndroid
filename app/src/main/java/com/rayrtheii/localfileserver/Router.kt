package com.rayrtheii.localfileserver

import android.content.Context
import fi.iki.elonen.router.RouterNanoHTTPD

class Router(port: Int, private val context: Context) : RouterNanoHTTPD(port) {
    init {
        addMappings()
        addBrowseMappings()
        start(SOCKET_READ_TIMEOUT, false)
    }

    private fun addBrowseMappings() {
        addRoute("/browse/", RouteHandler::class.java, context)
        addRoute("/browse/(.)+", RouteHandler::class.java, context)
    }
}