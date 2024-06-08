package com.example.data.datasource

import com.example.data.datasource.mockserver.MockWebServerPath
import com.example.data.datasource.mockserver.ProductMockDispatcher
import com.example.data.datasource.mockserver.ShoppingMockWebServer
import com.example.data.datasource.remote.RemoteProductDataSource
import com.example.data.datasource.remote.retrofit.RetrofitClient
import com.example.domain.datasource.ProductDataSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class RemoteCartDataSourceTest {
    private val server = ShoppingMockWebServer(ProductMockDispatcher())
    private val client = RetrofitClient(baseUrl = MockWebServerPath.BASE_URL)
    private val dataSource: ProductDataSource =
        RemoteProductDataSource(
            client.productService,
        )

    @BeforeEach
    fun setUp() {
        server.start()
    }

    @AfterEach
    fun close() {
        server.shutDown()
    }
    // val dataSource = RemoteCartDataSource()
}
