package woowacourse.shopping.viewmodel.cart

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.usecase.GetPagedCartProductsUseCase
import woowacourse.shopping.domain.usecase.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.UpdateQuantityUseCase
import woowacourse.shopping.fixture.FakeCartProductRepository
import woowacourse.shopping.view.cart.select.CartProductSelectViewModel
import woowacourse.shopping.viewmodel.CoroutinesTestExtension
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartProductSelectViewModelTest {
    private lateinit var viewModel: CartProductSelectViewModel
    private lateinit var cartProductRepository: CartProductRepository
    private lateinit var getPagedCartProductsUseCase: GetPagedCartProductsUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase
    private lateinit var updateQuantityUseCase: UpdateQuantityUseCase

    @BeforeEach
    fun setup() =
        runTest {
            cartProductRepository = FakeCartProductRepository()
            getPagedCartProductsUseCase = GetPagedCartProductsUseCase(cartProductRepository)
            removeFromCartUseCase = RemoveFromCartUseCase(cartProductRepository)
            updateQuantityUseCase = UpdateQuantityUseCase(cartProductRepository, removeFromCartUseCase)

            repeat(12) { id -> cartProductRepository.insert(id, 1) }
            viewModel =
                CartProductSelectViewModel(
                    getPagedCartProductsUseCase,
                    removeFromCartUseCase,
                    updateQuantityUseCase,
                )
            viewModel.loadPage(1)
        }

    @Test
    fun `초기 로드 시 첫 페이지의 상품이 로드된다`() {
        val products = viewModel.cartProductItems.getOrAwaitValue()
        assertAll(
            { assertEquals(5, products.size) },
            { assertEquals(1, viewModel.page.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(false, viewModel.hasPrevious.getOrAwaitValue()) },
            { assertEquals(false, viewModel.isSinglePage.getOrAwaitValue()) },
        )
    }

    @Test
    fun `다음 페이지 로드 시 페이지 번호가 증가하고 상품이 로드된다`() {
        // when
        viewModel.onNextPageClick()

        // then
        val products = viewModel.cartProductItems.getOrAwaitValue()
        assertAll(
            { assertEquals(5, products.size) },
            { assertEquals(2, viewModel.page.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasPrevious.getOrAwaitValue()) },
        )
    }

    @Test
    fun `마지막 페이지 로드 시 다음 페이지는 없다`() {
        // when
        viewModel.onNextPageClick()
        viewModel.onNextPageClick()

        // then
        assertAll(
            { assertEquals(2, viewModel.cartProductItems.getOrAwaitValue().size) },
            { assertEquals(3, viewModel.page.getOrAwaitValue()) },
            { assertEquals(false, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasPrevious.getOrAwaitValue()) },
        )
    }

    @Test
    fun `이전 페이지 로드 시 페이지 번호가 감소하고 상품이 로드된다`() {
        // when
        viewModel.onNextPageClick()
        viewModel.onPreviousPageClick()

        // then
        val products = viewModel.cartProductItems.value
        assertAll(
            { assertEquals(5, products?.size) },
            { assertEquals(1, viewModel.page.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(false, viewModel.hasPrevious.getOrAwaitValue()) },
        )
    }

    @Test
    fun `상품 수량 증가 클릭 시 수량이 1 증가한다`() {
        // given
        val cartProductItem =
            viewModel.cartProductItems
                .getOrAwaitValue()
                .first()
                .cartProduct
        // when
        viewModel.onQuantityIncreaseClick(cartProductItem)

        // then
        val updatedItem =
            viewModel.cartProductItems
                .getOrAwaitValue()
                .first { it.cartProduct.product.id == cartProductItem.product.id }
                .cartProduct
        assertEquals(2, updatedItem.quantity)
    }

    @Test
    fun `상품 수량 감소 클릭 시 수량이 1 감소한다`() {
        // given
        val cartProduct =
            viewModel.cartProductItems
                .getOrAwaitValue()
                .first()
                .cartProduct
        viewModel.onQuantityIncreaseClick(cartProduct)

        // when
        viewModel.onQuantityDecreaseClick(cartProduct)

        // then
        val updatedItem =
            viewModel.cartProductItems
                .getOrAwaitValue()
                .first { it.cartProduct.product.id == cartProduct.product.id }
                .cartProduct
        assertEquals(1, updatedItem.quantity)
    }
}
