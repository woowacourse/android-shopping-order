package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepositoryTest {
    private val remoteCartDataSource: RemoteCartDataSource = mockk()
    private lateinit var repository: CartRepository

    @BeforeEach
    fun setUp() {
        repository = DefaultCartRepository(remoteCartDataSource)
    }

    @Test
    fun `addCart - 성공적으로 cart를 추가하면 cartId를 반환한다`() =
        runTest {
            // given
            val cart = Cart(quantity = Quantity(2), productId = 1L)
            val expectedId = 10L

            coEvery { remoteCartDataSource.addCart(cart.toRequest()) } returns
                NetworkResult.Success(
                    expectedId,
                )

            // when
            val result = repository.addCart(cart)

            // then
            assertEquals(NetworkResult.Success(expectedId), result)
            coVerify(exactly = 1) { remoteCartDataSource.addCart(cart.toRequest()) }
        }

    @Test
    fun `loadPage - 첫 번째 페이지의 장바구니 상품 목록을 불러온다`() =
        runTest {
            val expectedPage = mockk<CartsSinglePage>()
            val expectedResult = NetworkResult.Success(expectedPage)

            coEvery { remoteCartDataSource.singlePage(1, 10) } returns expectedResult

            val result = repository.loadSinglePage(1, 10)

            assertEquals(expectedResult, result)
            coVerify(exactly = 1) { remoteCartDataSource.singlePage(1, 10) }
        }

    @Test
    fun `updateQuantity - 장바구니 상품의 수량을 업데이트한다`() =
        runTest {
            val cartId = 1L
            val quantity = Quantity(3)

            val expectedResult = NetworkResult.Success(Unit)

            coEvery { remoteCartDataSource.updateCartQuantity(cartId, quantity.value) } returns expectedResult

            // when
            val result = repository.updateQuantity(cartId, quantity)

            // then
            assertEquals(expectedResult, result)
            coVerify { remoteCartDataSource.updateCartQuantity(cartId, quantity.value) }
        }
}
