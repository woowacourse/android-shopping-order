package woowacourse.shopping.viewModel.products

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.fixture.PRODUCT1
import woowacourse.shopping.fixture.SHOPPING_CARTS
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCT1
import woowacourse.shopping.view.product.ProductsEvent
import woowacourse.shopping.view.product.ProductsItem
import woowacourse.shopping.view.product.viewModel.ProductsViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension
import woowacourse.shopping.viewModel.common.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class ProductsViewModelTest {
    private val productsRepository: ProductsRepository = mockk()
    private val shoppingCartRepository: ShoppingCartRepository = mockk()
    private lateinit var viewModel: ProductsViewModel

    @OptIn(DelicateCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        coEvery { productsRepository.getProduct(any()) } returns PRODUCT1
        coEvery { productsRepository.load(any(), any()) } returns listOf(PRODUCT1)
        coEvery { productsRepository.updateRecentWatchingProduct(any()) } just Runs
        coEvery { shoppingCartRepository.fetchAllQuantity() } returns 1
        coEvery {
            shoppingCartRepository.updateQuantity(
                any(), any(),
            )
        } returns SHOPPING_CART_PRODUCT1
        coEvery { shoppingCartRepository.load() } returns SHOPPING_CARTS

        viewModel =
            ProductsViewModel(
                productsRepository,
                shoppingCartRepository,
            )
    }

    @Test
    fun 상품을_장바구니에_추가할_수_있다() =
        runTest {
            // given
            val product =
                ProductsItem.ProductItem(
                    shoppingCartId = 1,
                    product = PRODUCT1,
                    selectedQuantity = 1,
                )

            // when
            viewModel.addProductToShoppingCart(product, product.selectedQuantity)

            // then
            coVerify { shoppingCartRepository.updateQuantity(1, 2) }
            coVerify { shoppingCartRepository.load(0, Int.MAX_VALUE) }
            coVerify { shoppingCartRepository.fetchAllQuantity() }
            coVerify { productsRepository.load(0, 20) }
        }

    @Test
    fun `상품을 장바구니에서 감소시킬 수 있다`() {
        // given
        val product =
            ProductsItem.ProductItem(
                shoppingCartId = 1,
                product = PRODUCT1,
                selectedQuantity = 1,
            )

        // when
        viewModel.minusProductToShoppingCart(product, product.selectedQuantity)

        // then
        coVerify { shoppingCartRepository.updateQuantity(1, 0) }
        coVerify { shoppingCartRepository.load(0, Int.MAX_VALUE) }
        coVerify { shoppingCartRepository.fetchAllQuantity() }
        coVerify { productsRepository.load(0, 20) }
    }

    @Test
    fun `현재 데이터를 다시 로드할 수 있다`() {
        // when
        viewModel.reload()

        // then
        coVerify { productsRepository.load(0, 20) }
        coVerify { shoppingCartRepository.load(0, Int.MAX_VALUE) }
        coVerify { shoppingCartRepository.fetchAllQuantity() }
    }

    @Test
    fun `다음 페이지의 상풂을 추가 로드할 수 있다`() {
        // when
        viewModel.updateMoreProducts()

        // then
        coVerify { productsRepository.load(1, 20) }
        coVerify { shoppingCartRepository.load(0, Int.MAX_VALUE) }
        coVerify { shoppingCartRepository.fetchAllQuantity() }
    }

    @Test
    fun `장바구니의 총 수량을 로드할 수 있다`() {
        // given
        val expected = 1

        // when
        viewModel =
            ProductsViewModel(
                productsRepository,
                shoppingCartRepository,
            )

        // then
        coVerify { shoppingCartRepository.fetchAllQuantity() }
        assertThat(
            viewModel.shoppingCartQuantity.getOrAwaitValue(),
        ).isEqualTo(expected)
    }

    @Test
    fun `상품과 장바구니 수량을 합친 uiModel을 로드할 수 있다`() {
        // when
        viewModel =
            ProductsViewModel(
                productsRepository,
                shoppingCartRepository,
            )

        // then
        assertThat(viewModel.products.getOrAwaitValue()).isEqualTo(
            listOf(
                ProductsItem.ProductItem(
                    shoppingCartId = 1,
                    product = PRODUCT1,
                    selectedQuantity = 1,
                ),
            ),
        )
    }

    @Test
    fun `상품 로드에 실패하면 이벤트를 발생시킨다`() {
        // given
        coEvery { productsRepository.load(any(), any()) } throws Exception()

        // when
        viewModel =
            ProductsViewModel(
                productsRepository,
                shoppingCartRepository,
            )

        // then
        assertThat(viewModel.event.getValue()).isEqualTo(ProductsEvent.UPDATE_PRODUCT_FAILURE)
    }

    @Test
    fun `모든 데이터가 로드되면 로딩 플래그가 false가 된다`() {
        // given
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        dispatcher.scheduler.runCurrent()

        // when
        viewModel =
            ProductsViewModel(
                productsRepository,
                shoppingCartRepository,
            )

        // then
        assertThat(viewModel.isLoading.getOrAwaitValue()).isTrue()
        dispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.isLoading.getOrAwaitValue()).isFalse()

        Dispatchers.resetMain()
    }
}
