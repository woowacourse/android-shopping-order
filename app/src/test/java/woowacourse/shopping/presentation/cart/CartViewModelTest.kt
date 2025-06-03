package woowacourse.shopping.presentation.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.FakeCartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CartViewModel

    @Test
    fun `초기에는 첫 페이지 상품만 보여준다`() {
        viewModel =
            CartViewModel(
                FakeCartItemRepository(0),
            )

        val products = viewModel.pagingData.value?.products ?: emptyList()

        assertThat(products.size).isEqualTo(0)
        assertThat(viewModel.getPage()).isEqualTo(0)
    }

    @Test
    fun `다음 페이지 버튼은 다음 페이지가 있을 때만 활성화된다`() {
        viewModel =
            CartViewModel(
                FakeCartItemRepository(10),
            )
        assertThat(viewModel.hasNextPage()).isTrue

        repeat(2) { viewModel.onNextPage() }

        assertThat(viewModel.hasNextPage()).isFalse
    }

    @Test
    fun `이전 페이지 버튼은 첫 페이지가 아닐 때만 활성화된다`() {
        viewModel =
            CartViewModel(
                FakeCartItemRepository(10),
            )

        assertThat(viewModel.hasPrevPage()).isFalse

        viewModel.onNextPage()
        assertThat(viewModel.hasPrevPage()).isTrue
    }

    @Test
    fun `상품의 수량을 증가시킬 수 있다`() {
        val cartRepository = FakeCartItemRepository(1)
        val product =
            ProductUiModel(
                id = 1L,
                name = "아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 4000,
                quantity = 2,
            )

        cartRepository.addCartItem(product.id, product.quantity) {}

        viewModel = CartViewModel(cartRepository)

        viewModel.increaseQuantity(product)

        val updated = viewModel.product.value
        assertThat(updated?.quantity).isEqualTo(3)
    }

    @Test
    fun `상품의 수량을 감소시킬 수 있다`() {
        val cartRepository = FakeCartItemRepository(1)
        val product =
            ProductUiModel(
                id = 1L,
                name = "아이스 카페 아메리카노",
                imageUrl = "https://example.com/image.jpg",
                price = 4000,
                quantity = 3,
            )

        cartRepository.addCartItem(product.id, product.quantity) {}

        viewModel = CartViewModel(cartRepository)

        viewModel.decreaseQuantity(product)

        val updated = viewModel.product.getOrAwaitValue()
        assertThat(updated.quantity).isEqualTo(2)
    }

    @Test
    fun `상품 수량은 최소 1까지 감소 가능하다`() {
        val cartRepository = FakeCartItemRepository(1)
        val product =
            ProductUiModel(
                id = 1L,
                name = "아이스 카페 아메리카노",
                imageUrl = "https://example.com/image.jpg",
                price = 4000,
                quantity = 1,
            )

        cartRepository.addCartItem(product.id, product.quantity) {}

        viewModel = CartViewModel(cartRepository)

        viewModel.decreaseQuantity(product)

        val updated = viewModel.product.getOrAwaitValue()
        assertThat(updated.quantity).isEqualTo(1)
    }
}