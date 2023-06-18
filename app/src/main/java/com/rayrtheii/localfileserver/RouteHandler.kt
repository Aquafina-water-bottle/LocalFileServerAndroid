package com.rayrtheii.localfileserver

import android.content.Context
import android.text.Html
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response.IStatus
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse
import fi.iki.elonen.router.RouterNanoHTTPD
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URLEncoder

const val BASE_HTML = """
<!DOCTYPE html>
<style>
    body {
        font-size: 32px;
    }
</style>
<html>
    <head>
        <title>{{TITLE}}</title>
    </head>
    <body>
        {{BODY}}
    </body>
</html>
"""

data class Page(val mimeType: String, val data: ByteArray)

val fileEndingToMimeTypeMap = mapOf(
    "html" to "text/html",
    "txt" to "text/plain",
    "png" to "image/png",
    "jpg" to "image/jpeg",
    "jpeg" to "image/jpeg",
    "webp" to "image/webp",
)

class RouteHandler : RouterNanoHTTPD.DefaultHandler() {

    //private lateinit var routing: Routing
    private lateinit var context: Context
    override fun getText(): String {
        return "not implemented"
    }

    override fun getMimeType(): String {
        return "text/html"
    }

    override fun getStatus(): IStatus {
        return NanoHTTPD.Response.Status.OK
    }

    private fun baseHtmlWrapper(title: String, body: String): String {
        return BASE_HTML.replace("{{TITLE}}", title).replace("{{BODY}}", body)
    }

    private fun pageFromPath(path: File, searchPath: String): Page {

        if (path.isDirectory) { // lists out directory in simple list
            val list = path.list()
            val builder = StringBuilder()
            val dirEscaped = Html.fromHtml(searchPath, Html.FROM_HTML_MODE_LEGACY)

            builder.append("<p>Directory: ${dirEscaped}</p>")
            if (list == null) {
                builder.append("<ol><li>Empty directory</li></ol>")
            } else {
                builder.append("<ol>")
                for (child in list) {
                    var childEscaped = Html.fromHtml(child, Html.FROM_HTML_MODE_LEGACY).toString()

                    val childPath = File(searchPath, child)
                    if (childPath.isDirectory) {
                        childEscaped += "/"
                    }

                    val childUrl = "http://localhost:$PORT/browse/${URLEncoder.encode(searchPath, "utf-8")}/${URLEncoder.encode(child, "utf-8")}"
                    builder.append("""<li><a href="$childUrl">${childEscaped}</a></li>""")
                }
                builder.append("</ol>")
            }
            return Page("text/html", baseHtmlWrapper("Directory", builder.toString()).toByteArray())
        } else if (path.isFile) {
            for ((fileEnding, mimeType) in fileEndingToMimeTypeMap) {
                if (path.extension == fileEnding) {
                    return Page(mimeType, path.readBytes())
                }
            }
            return Page("text/plain", "File has an unsupported file ending".toByteArray())
        }

        return Page("text/plain", "File not found".toByteArray())
    }

    override fun get(
        uriResource: UriResource,
        urlParams: Map<String, String>,
        session: IHTTPSession
    ): NanoHTTPD.Response {
        if (!this::context.isInitialized) {
            // context passed in from initialization
            context = uriResource.initParameter(
                0,
                Context::class.java
            )
        }

        val uri = session.uri
        val components = uri.split("/", limit=3)
        var filePath = ""
        if (components.size > 2) {
            filePath = components[2]
        }
        val fullPath = File(context.getExternalFilesDir(null), filePath)
        val page = pageFromPath(fullPath, filePath)

        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, page.mimeType,
            ByteArrayInputStream(page.data),
            page.data.size.toLong()
        )
    }
}