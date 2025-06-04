package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepositoryTest {
    private val cartDataSource: CartDataSource = mockk()
    private lateinit var repository: CartRepository

    @BeforeEach
    fun setUp() {
        repository = DefaultCartRepository(cartDataSource)
    }

    @Test
    fun `addCart - 성공적으로 cart를 추가하면 cartId를 반환한다`() =
        runTest {
            // given
            val cart = Cart(quantity = Quantity(2), productId = 1L)
            val expectedId = 10L

            coEvery { cartDataSource.addCart(cart.toRequest()) } returns
                NetworkResult.Success(
                    expectedId,
                )

            // when
            val result = repository.addCart(cart)

            // then
            assertEquals(NetworkResult.Success(expectedId), result)
            coVerify(exactly = 1) { cartDataSource.addCart(cart.toRequest()) }
        }

    @Test
    fun `loadPage - 첫 번째 페이지의 장바구니 상품 목록을 불러온다`() =
        runTest {
            val expectedPage = mockk<CartsSinglePage>()
            val expectedResult = NetworkResult.Success(expectedPage)

            coEvery { cartDataSource.singlePage(1, 10) } returns expectedResult

            val result = repository.loadSinglePage(1, 10)

            assertEquals(expectedResult, result)
            coVerify(exactly = 1) { cartDataSource.singlePage(1, 10) }
        }
}
