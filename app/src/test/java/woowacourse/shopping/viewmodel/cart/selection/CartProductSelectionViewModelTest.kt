package woowacourse.shopping.viewmodel.cart.selection

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.fixture.FakeCartProductRepository
import woowacourse.shopping.view.cart.selection.CartProductSelectionViewModel
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class)
class CartProductSelectionViewModelTest {
    private lateinit var viewModel: CartProductSelectionViewModel
    private lateinit var cartProductRepository: CartProductRepository

    @BeforeEach
    fun setup() {
        cartProductRepository = FakeCartProductRepository()
        repeat(12) { id -> cartProductRepository.insert(id, 1) {} }
        viewModel = CartProductSelectionViewModel(cartProductRepository)
        viewModel.loadPage(1)
    }

    @Test
    fun `초기 로드 시 첫 페이지의 상품이 로드된다`() {
        val products = viewModel.products.getOrAwaitValue()
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
        viewModel.loadNextProducts()

        // then
        val products = viewModel.products.getOrAwaitValue()
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
        viewModel.loadNextProducts()
        viewModel.loadNextProducts()

        // then
        assertAll(
            { assertEquals(2, viewModel.products.getOrAwaitValue().size) },
            { assertEquals(3, viewModel.page.getOrAwaitValue()) },
            { assertEquals(false, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasPrevious.getOrAwaitValue()) },
        )
    }

    @Test
    fun `이전 페이지 로드 시 페이지 번호가 감소하고 상품이 로드된다`() {
        // when
        viewModel.loadNextProducts()
        viewModel.loadPreviousProducts()

        // then
        val products = viewModel.products.value
        assertAll(
            { assertEquals(5, products?.size) },
            { assertEquals(1, viewModel.page.getOrAwaitValue()) },
            { assertEquals(true, viewModel.hasNext.getOrAwaitValue()) },
            { assertEquals(false, viewModel.hasPrevious.getOrAwaitValue()) },
        )
    }

    @Test
    fun `상품 제거 시 repository에서 제거된다`() {
        // given
        val productToRemove = viewModel.products.getOrAwaitValue().first()

        // when
        viewModel.onProductRemoveClick(productToRemove.cartProduct)

        // then
        assertEquals(false, viewModel.products.getOrAwaitValue().contains(productToRemove))
    }

    @Test
    fun `마지막 아이템을 삭제하면 이전 페이지로 이동한다`() {
        // given
        viewModel.loadNextProducts()
        viewModel.loadNextProducts()

        // when
        viewModel.onProductRemoveClick(
            viewModel.products
                .getOrAwaitValue()
                .last()
                .cartProduct,
        )
        viewModel.onProductRemoveClick(
            viewModel.products
                .getOrAwaitValue()
                .last()
                .cartProduct,
        )

        // then
        assertEquals(2, viewModel.page.getOrAwaitValue())
        assertTrue(viewModel.products.getOrAwaitValue().isNotEmpty())
    }

    @Test
    fun `상품 수량 증가 클릭 시 수량이 1 증가한다`() {
        // given
        val cartProductItem =
            viewModel.products
                .getOrAwaitValue()
                .first()
                .cartProduct
        // when
        viewModel.onQuantityIncreaseClick(cartProductItem)

        // then
        val updatedItem =
            viewModel.products
                .getOrAwaitValue()
                .first { it.cartProduct.product.id == cartProductItem.product.id }
                .cartProduct
        assertEquals(2, updatedItem.quantity)
    }

    @Test
    fun `상품 수량 감소 클릭 시 수량이 1 감소한다`() {
        // given
        val cartProduct =
            viewModel.products
                .getOrAwaitValue()
                .first()
                .cartProduct
        viewModel.onQuantityIncreaseClick(cartProduct)

        // when
        viewModel.onQuantityDecreaseClick(cartProduct)

        // then
        val updatedItem =
            viewModel.products
                .getOrAwaitValue()
                .first { it.cartProduct.product.id == cartProduct.product.id }
                .cartProduct
        assertEquals(1, updatedItem.quantity)
    }

    @Test
    fun `상품 선택 시 선택된 상품에 추가된다`() {
        // given
        val cartProduct =
            viewModel.products
                .getOrAwaitValue()
                .first()
                .cartProduct

        // when
        viewModel.onSelectItem(cartProduct)
        val actual = viewModel.selectedIds

        // then
        actual shouldContain cartProduct.id
    }

    @Test
    fun `상품 전체 선택 시 전체 상품이 선택된 상품에 추가된다`() {
        // given
        val cartProducts =
            viewModel.products
                .getOrAwaitValue()
                .map { it.cartProduct }

        // when
        viewModel.onSelectAllItems()
        val actual = viewModel.selectedIds

        // then
        actual shouldContainAll cartProducts.map { it.id }
    }

    @Test
    fun `전체 상품이 담긴 상태에서 상품 전체 선택 시 선택된 상품에서 제거된다`() {
        // given
        viewModel.onSelectAllItems()

        // when
        viewModel.onSelectAllItems()
        val actual = viewModel.selectedIds

        // then
        actual shouldHaveSize 0
    }
}
