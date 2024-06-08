package com.example.data.datasource

import com.example.data.datasource.mockserver.MockWebProductServer
import com.example.data.datasource.mockserver.MockWebProductServerDispatcher
import com.example.data.datasource.mockserver.MockWebServerPath
import com.example.data.datasource.remote.RemoteProductDataSource
import com.example.data.datasource.remote.retrofit.RetrofitClient
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.ProductDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.dummy.dummyProductList

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class RemoteProductDataSourceTest {
    private val server = MockWebProductServer(MockWebProductServerDispatcher())
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

    @Test
    fun `물품 개수가 20개인 첫번째 페이지를 가져온다`() {
        val list = dummyProductList.subList(0, 20).toList()
        val expected = DataResponse.Success(list)
        val actual = dataSource.findRange(0, 20)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `product id가 1인 물품을 가져온다`() {
        val productId = 1
        val product = dummyProductList.find { it.id == productId }
        val expected = DataResponse.Success(product)
        val actual = dataSource.find(productId)
        assertThat(actual).isEqualTo(expected)
    }
}
