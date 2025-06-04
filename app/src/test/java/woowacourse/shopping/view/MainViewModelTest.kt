package woowacourse.shopping.view

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.ext.getOrAwaitValue
import woowacourse.shopping.fixture.productFixture1
import woowacourse.shopping.fixture.productFixture2
import woowacourse.shopping.view.main.vm.MainViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class MainViewModelTest {
    private val historyRepository: HistoryRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val cartRepository: CartRepository = mockk()
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setup() {
        viewModel = MainViewModel(historyRepository, productRepository, cartRepository)
    }

    @Test
    fun `초기 로딩 시 제품과 장바구니 데이터를 로드한다`() =
        runTest {
            // given
            val products = listOf(productFixture1, productFixture2)
            val productPage = ProductSinglePage(products = products, hasNextPage = true)

            val carts =
                listOf(
                    ShoppingCart(id = 10L, product = productFixture1, quantity = Quantity(2)),
                )
            val cartPage = CartsSinglePage(carts = carts, hasNextPage = false)

            coEvery { productRepository.loadSinglePage(page = 0, pageSize = 20) } returns NetworkResult.Success(productPage)
            coEvery { cartRepository.loadSinglePage(0, 20) } returns NetworkResult.Success(cartPage)
            coEvery { historyRepository.getHistories() } returns emptyList()

            // when
            viewModel.loadPage()

            // then
            val state = viewModel.uiState.getOrAwaitValue()
            assertEquals(productPage.products, state.productItems.map { it.item })
        }

    @Test
    fun `최근본 상품을 동기화한다`() =
        runTest {
            // given
            val productId = 1L
            val histories = listOf(productId)
            val product = productFixture1

            coEvery { historyRepository.getHistories() } returns histories
            coEvery { productRepository.loadProduct(productId) } returns NetworkResult.Success(product)

            // when
            viewModel.syncHistory()

            // then
            val state = viewModel.uiState.value
            assertEquals(1, state?.historyItems?.size)
            assertEquals(productId, state?.historyItems?.first()?.productId)
        }
}
