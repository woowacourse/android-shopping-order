package woowacourse.shopping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goods.GoodsViewModel
import woowacourse.shopping.feature.model.GoodsItem
import woowacourse.shopping.feature.model.State
import woowacourse.shopping.fixture.CartResponseFixture
import woowacourse.shopping.fixture.ProductResponseFixture
import woowacourse.shopping.fixture.dummyCartProduct
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@ExtendWith(InstantTaskExecutorExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GoodsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GoodsViewModel

    private lateinit var cartRepo: CartRepository
    private lateinit var productRepo: ProductRepository
    private lateinit var historyRepo: HistoryRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock 초기화
        cartRepo = mock()
        productRepo = mock()
        historyRepo = mock()

        runTest {
            stubDefaultResponses()
        }

        viewModel = GoodsViewModel(historyRepo, productRepo, cartRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun stubDefaultResponses() {
        whenever(cartRepo.fetchAllCart()).thenReturn(
            CartResponseFixture.createCartResponse(),
        )
        whenever(cartRepo.getCartCounts()).thenReturn(Result.success(2))
        whenever(productRepo.fetchProducts(any())).thenReturn(
            ProductResponseFixture.createProductResponse(),
        )
        whenever(historyRepo.getAll()).thenReturn(emptyList())
    }

    @Test
    fun `초기 로딩시 장바구니 아이템과 총 수량이 LiveData로 반영된다`() =
        runTest {
            advanceUntilIdle()

            val items = viewModel.items.getOrAwaitValue()
            val total = viewModel.totalQuantity.getOrAwaitValue()

            assertTrue(items.isNotEmpty())
            assertEquals(2, total)
        }

    @Test
    fun `addToCart 호출 시 insertState가 Success 상태로 설정된다`() =
        runTest {
            advanceUntilIdle()

            val newCart = dummyCartProduct.copy(id = 0L, quantity = 0)

            whenever(cartRepo.addToCart(any())).thenReturn(Result.success(99L))
            whenever(cartRepo.getCartCounts()).thenReturn(Result.success(1))

            viewModel.addToCart(newCart)

            advanceUntilIdle()

            val state = viewModel.insertState.getOrAwaitValue().peekContent()
            assertTrue(state is State.Success)
        }

    @Test
    fun `수량이 1 이상일 때 addToCart 호출 시 기존 아이템의 수량이 증가한다`() =
        runTest {
            advanceUntilIdle()

            val existingCart = dummyCartProduct.copy(quantity = 1)

            whenever(cartRepo.updateCart(id = existingCart.id, cartQuantity = CartQuantity(2)))
                .thenReturn(Result.success(Result.success(Unit)))
            whenever(cartRepo.getCartCounts()).thenReturn(Result.success(2))

            viewModel.addToCart(existingCart)

            advanceUntilIdle()

            val updated =
                viewModel.items
                    .getOrAwaitValue()
                    .filterIsInstance<GoodsItem.Product>()
                    .find { it.cart.product.id == existingCart.product.id }

            assertNotNull(updated)
            assertEquals(2, updated.cart.quantity)
        }

    @Test
    fun `수량이 2 이상일 때 removeFromCart 호출 시 수량이 감소한다`() =
        runTest {
            advanceUntilIdle()

            val target = dummyCartProduct.copy(quantity = 2)

            whenever(cartRepo.updateCart(id = target.id, cartQuantity = CartQuantity(1)))
                .thenReturn(Result.success(Result.success(Unit)))
            whenever(cartRepo.getCartCounts()).thenReturn(Result.success(1))

            viewModel.removeFromCart(target)

            advanceUntilIdle()

            val updated =
                viewModel.items
                    .getOrAwaitValue()
                    .filterIsInstance<GoodsItem.Product>()
                    .find { it.cart.product.id == target.product.id }

            assertNotNull(updated)
            assertEquals(1, updated.cart.quantity)
        }

    @Test
    fun `수량이 1일 때 removeFromCart 호출 시 장바구니에서 해당 상품을 제거한다`() =
        runTest {
            val target = dummyCartProduct.copy(quantity = 1)

            whenever(cartRepo.deleteCart(id = target.id)).thenReturn(Result.success(Unit))
            whenever(cartRepo.getCartCounts()).thenReturn(Result.success(0))

            viewModel.removeFromCart(target)

            advanceUntilIdle()

            val updated =
                viewModel.items
                    .getOrAwaitValue()
                    .filterIsInstance<GoodsItem.Product>()
                    .find { it.cart.product.id == target.product.id }

            assertNotNull(updated)
            assertEquals(0, updated.cart.quantity)
            assertEquals(0L, updated.cart.id)
        }
}
