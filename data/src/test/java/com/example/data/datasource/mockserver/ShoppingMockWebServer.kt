package com.example.data.datasource.mockserver

import com.example.data.datasource.mockserver.MockWebServerPath.BASE_PORT
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer

class ShoppingMockWebServer(
    private val dispatcher: Dispatcher,
) {
    val server: MockWebServer = MockWebServer()

    fun start() {
        server.dispatcher = dispatcher
        server.start(BASE_PORT)
    }

    fun shutDown() {
        server.shutdown()
    }
}
