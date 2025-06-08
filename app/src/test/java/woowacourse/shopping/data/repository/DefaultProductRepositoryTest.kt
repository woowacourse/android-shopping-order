package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.remote.RemoteProductsDataSource
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepositoryTest {
    private val productDateSource = mockk<RemoteProductsDataSource>()
    private lateinit var repository: ProductRepository

    @BeforeEach
    fun setUp() {
        repository = DefaultProductRepository(productDateSource)
    }

    @Test
    fun `첫 번째 페이지 데이터를 반환한다`() =
        runTest {
            // given
            val expected = ProductSinglePage(products = listOf(), false)

            coEvery {
                productDateSource.singlePage(null, page = 1, size = 20)
            } returns Result.success(expected)

            val result = productDateSource.singlePage(null, page = 1, size = 20)
            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
        }

    @Test
    fun `요청 중 예외가 발생하면 실패 결과를 반환한다`() =
        runTest {
            // given
            val exception = NetworkError.UnknownError

            coEvery {
                productDateSource.singlePage(null, page = 1, size = 20)
            } returns Result.failure(exception)

            // when
            val result = productDateSource.singlePage(null, page = 1, size = 20)

            // then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    @Test
    fun `하나의 Product에 대한 정보를 반환한다`() =
        runTest {
            // given
            val productId = 1L
            val expected = Product(productId, "짜파게티", "", "", Price(1), Quantity(1))
            coEvery {
                productDateSource.getProduct(productId)
            } returns Result.success(expected)

            // when
            val result = repository.loadProduct(productId)
            assertTrue(result.isFailure)
            assertEquals(expected, result.getOrNull())
        }
}
