package com.example.data.datasource

import com.example.data.datasource.mockserver.CartMockDispatcher
import com.example.data.datasource.mockserver.MockWebServerPath
import com.example.data.datasource.mockserver.ShoppingMockWebServer
import com.example.data.datasource.mockserver.dummyCartItems
import com.example.data.datasource.remote.RemoteCartDataSource
import com.example.data.datasource.remote.retrofit.RetrofitClient
import com.example.domain.datasource.CartDataSource
import com.example.domain.datasource.DataResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoteCartDataSourceTest {
    private val server = ShoppingMockWebServer(CartMockDispatcher())
    private val client = RetrofitClient(baseUrl = MockWebServerPath.BASE_URL)
    private val dataSource: CartDataSource =
        RemoteCartDataSource(
            client.cartItemService,
        )

    @BeforeEach
    fun setUp() {
        server.start()
    }

    @AfterEach
    fun close() {
        server.shutDown()
    }

    @Test
    fun `페이지 사이즈가 5일 때 첫 번째 장바구니 페이지를 가져온다`() {
        val list = dummyCartItems.subList(0, 5).toList()
        val expected = DataResponse.Success(list)
        val actual = dataSource.findRange(0, 5)
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
