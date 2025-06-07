package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepositoryTest {
    private val productDateSource = mockk<ProductsDataSource>()
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
            } returns NetworkResult.Success(expected)

            val result = productDateSource.singlePage(null, page = 1, size = 20)
            assertTrue(result is NetworkResult.Success)
            assertEquals(expected, (result as NetworkResult.Success).value)
        }

    @Test
    fun `하나의 Product에 대한 정보를 반환한다`() =
        runTest {
            // given
            val productId = 1L
            val expected = Product(productId, "짜파게티", "", "", Price(1), Quantity(1))
            coEvery {
                productDateSource.getProduct(productId)
            } returns NetworkResult.Success(expected)

            // when
            val result = repository.loadProduct(productId)
            assertTrue(result is NetworkResult.Success)
            assertEquals(expected, (result as NetworkResult.Success).value)
        }
}
