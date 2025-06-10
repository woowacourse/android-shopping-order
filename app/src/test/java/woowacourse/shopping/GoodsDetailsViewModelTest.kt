package woowacourse.shopping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsViewModel
import woowacourse.shopping.feature.model.State
import woowacourse.shopping.fixture.CartResponseFixture
import woowacourse.shopping.fixture.ProductResponseFixture
import woowacourse.shopping.fixture.dummyHistory
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GoodsDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var cartRepo: CartRepository
    private lateinit var historyRepo: HistoryRepository
    private lateinit var productRepo: ProductRepository

    private lateinit var viewModel: GoodsDetailsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        cartRepo = mock()
        historyRepo = mock()
        productRepo = mock()

        runTest {
            stubDefaultResponses()
        }

        viewModel = GoodsDetailsViewModel(cartRepo, historyRepo, productRepo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun stubDefaultResponses() {
        whenever(historyRepo.findLatest()).thenReturn(dummyHistory)
        whenever(historyRepo.getAll()).thenReturn(listOf(dummyHistory))
        whenever(productRepo.requestProductDetails(1L)).thenReturn(ProductResponseFixture.createProductResponse().content.first())
        whenever(cartRepo.findCartByProductId(1L)).thenReturn(CartResponseFixture.createCartResponse().content.first())
    }

    @Test
    fun `수량 증가시 장바구니 수량이 증가한다`() =
        runTest {
            viewModel.setInitialCart(1)
            advanceUntilIdle()

            viewModel.increaseQuantity()
            advanceUntilIdle()

            val updatedCart = viewModel.cartProduct.getOrAwaitValue()
            assertEquals(2, updatedCart.quantity)
        }

    @Test
    fun `수량 감소시 장바구니 수량이 감소한다`() =
        runTest {
            viewModel.setInitialCart(1)
            advanceUntilIdle()

            viewModel.decreaseQuantity()
            advanceUntilIdle()

            val updatedCart = viewModel.cartProduct.getOrAwaitValue()
            assertEquals(0, updatedCart.quantity)
        }

    @Test
    fun `초기 장바구니 상품 세팅 시 상품 정보가 정상적으로 로드된다`() =
        runTest {
            viewModel.setInitialCart(1)
            advanceUntilIdle()

            val cartProduct = viewModel.cartProduct.getOrAwaitValue()
            assertEquals(1L, cartProduct.product.id)
            assertEquals(1, cartProduct.quantity)
        }

    @Test
    fun `commitCart 호출 시 수량이 1이면 장바구니에 새로 추가된다`() =
        runTest {
            viewModel.setInitialCart(1)
            advanceUntilIdle()

            whenever(cartRepo.addToCart(any())).thenReturn(Result.success(100L))

            viewModel.commitCart()
            advanceUntilIdle()

            val state = viewModel.insertState.getOrAwaitValue().peekContent()
            assertTrue(state is State.Success)
        }

    @Test
    fun `commitCart 호출 시 수량이 2 이상이면 기존 장바구니 상품이 업데이트 된다`() =
        runTest {
            viewModel.setInitialCart(1)
            advanceUntilIdle()

            // 수량 2로 변경
            viewModel.increaseQuantity()
            advanceUntilIdle()

            whenever(cartRepo.updateCart(any(), any())).thenReturn(Result.success(Result.success(Unit)))

            viewModel.commitCart()
            advanceUntilIdle()

            val state = viewModel.insertState.getOrAwaitValue().peekContent()
            assertTrue(state is State.Success)
        }

    @Test
    fun `loadLastViewed 호출 시 가장 최근 본 상품이 로드된다`() =
        runTest {
            viewModel.loadLastViewed()
            advanceUntilIdle()

            val lastViewed = viewModel.lastViewed.getOrAwaitValue()
            assertEquals(dummyHistory.id, lastViewed.id)
        }
}
