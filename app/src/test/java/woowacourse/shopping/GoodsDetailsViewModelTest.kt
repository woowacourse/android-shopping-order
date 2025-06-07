package woowacourse.shopping

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsViewModel
import woowacourse.shopping.fixture.CartResponseFixture
import woowacourse.shopping.fixture.ProductResponseFixture
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
}
