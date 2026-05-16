package woowacourse.shopping.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import woowacourse.shopping.data.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.RetrofitServices
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.fake.FakeCartDispatcher
import woowacourse.shopping.fake.FakeProductDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultProductRepositoryTest {
    private lateinit var server: MockWebServer

    private val product =
        Product(
            id = 1L,
            name = ProductName(name = "상품"),
            price = Money(amount = 5000),
            imageUrl = "",
            category = "",
        )

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.dispatcher =
            FakeProductDispatcher(
                size = 100,
                product = product,
            )
        server.start()
    }

    @ParameterizedTest
    @CsvSource("0, 10", "20,30")
    fun `레파지토리에서 상품 정보를 가져온다`(
        offset: Int,
        limit: Int,
    ) = runTest {
        val defaultProductRepository =
            DefaultProductRepository(
                ProductRemoteDataSource(
                    productService = RetrofitServices(server.url("/").toString(), FakeCartDispatcher.authToken).productService,
                ),
            )

        val products = defaultProductRepository.getProducts(offset, limit)
        advanceUntilIdle()
        assertThat(products.size).isEqualTo(limit)
    }

    @Test
    fun `상품의 ID로 단일 상품을 조회한다`() =
        runTest {
            val defaultProductRepository =
                DefaultProductRepository(
                    ProductRemoteDataSource(
                        RetrofitServices(server.url("/").toString(), FakeCartDispatcher.authToken).productService,
                    ),
                )

            val product = defaultProductRepository.getProductById(1L)
            advanceUntilIdle()
            assertThat(product.id).isEqualTo(1L)
        }
}
