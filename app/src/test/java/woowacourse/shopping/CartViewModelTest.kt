package woowacourse.shopping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartQuantity
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.fixture.CartResponseFixture
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CartViewModel
    private lateinit var cartRepo: CartRepository
    private lateinit var productRepo: ProductRepository
    private lateinit var historyRepo: HistoryRepository

    private val dummyProduct =
        Product(
            id = 1L,
            name = "테스트상품",
            price = 1000,
            imageUrl = "https://example.com",
            category = "테스트",
        )

    private val dummyCart =
        CartProduct(
            id = 1L,
            product = dummyProduct,
            quantity = 2,
        )

    private val dummyHistory =
        History(
            id = 1L,
            name = "테스트상품",
            thumbnailUrl = "",
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        cartRepo = mock()
        productRepo = mock()
        historyRepo = mock()

        runTest {
            stubDefaultResponses()
        }

        viewModel = CartViewModel(cartRepo, productRepo, historyRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun stubDefaultResponses() {
        val cartResponse = CartResponseFixture.createCartResponse()

        whenever(cartRepo.getCartCounts()).thenReturn(Result.success(1))
        whenever(cartRepo.fetchAllCart()).thenReturn(cartResponse)
        whenever(cartRepo.fetchCart(page = any())).thenReturn(cartResponse)
        whenever(historyRepo.findLatest()).thenReturn(dummyHistory)
        whenever(productRepo.fetchRecommendProducts(any(), any())).thenReturn(emptyList())
    }

//    @Test
//    fun `plusPage 호출 시 페이지가 증가한다`() =
//        runTest {
//            advanceUntilIdle()
//            assertEquals(0, viewModel.page.getOrAwaitValue())
//
//            viewModel.plusPage()
//            advanceUntilIdle()
//
//            assertEquals(1, viewModel.page.getOrAwaitValue())
//        }
//
//    @Test
//    fun `minusPage 호출 시 페이지가 감소한다`() =
//        runTest {
//            advanceUntilIdle()
//
//            viewModel.plusPage()
//            advanceUntilIdle()
//            assertEquals(1, viewModel.page.getOrAwaitValue())
//
//            viewModel.minusPage()
//            advanceUntilIdle()
//            assertEquals(0, viewModel.page.getOrAwaitValue())
//        }

    @Test
    fun `addToCart 호출 시 수량이 증가한다`() =
        runTest {
            advanceUntilIdle()

            whenever(cartRepo.updateCart(id = dummyCart.id, cartQuantity = CartQuantity(3)))
                .thenReturn(Result.success(Result.success(Unit)))

            viewModel.addToCart(dummyCart)
            advanceUntilIdle()

            val updatedCart = viewModel.carts.getOrAwaitValue().find { it.id == dummyCart.id }
            assertNotNull(updatedCart)
            assertEquals(1, updatedCart.quantity)
        }

    @Test
    fun `removeFromCart 호출 시 수량이 감소한다`() =
        runTest {
            advanceUntilIdle()

            whenever(cartRepo.updateCart(id = dummyCart.id, cartQuantity = CartQuantity(1)))
                .thenReturn(Result.success(Result.success(Unit)))

            viewModel.removeFromCart(dummyCart)
            advanceUntilIdle()

            val updatedCart = viewModel.carts.getOrAwaitValue().find { it.id == dummyCart.id }
            assertNotNull(updatedCart)
            assertEquals(1, updatedCart.quantity)
        }

    @Test
    fun `removeFromCart 호출 시 수량이 0이면 아이템 삭제된다`() =
        runTest {
            advanceUntilIdle()

            val oneQuantityCart = dummyCart.copy(quantity = 1)

            whenever(cartRepo.deleteCart(oneQuantityCart.id)).thenReturn(Result.success(Unit))
            whenever(cartRepo.getCartCounts()).thenReturn(Result.success(0))

            viewModel.removeFromCart(oneQuantityCart)
            advanceUntilIdle()

            val updatedList = viewModel.carts.getOrAwaitValue()
            assertTrue(updatedList.none { it.id == oneQuantityCart.id })
        }
}
