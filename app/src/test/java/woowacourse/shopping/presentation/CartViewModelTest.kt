package woowacourse.shopping.presentation

import androidx.lifecycle.MutableLiveData
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.presentation.cart.CartViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = FakeCartRepository()
        productRepository = mockk<ProductRepository>(relaxed = true)
        viewModel = CartViewModel(cartRepository)
        (viewModel.totalPages as MutableLiveData).value = 5
    }

    @Test
    fun `장바구니_상품을_불러온다`() {
        viewModel.loadItems(0)

        val result = viewModel.products.getOrAwaitValue()
        assertThat(result).isInstanceOf(ResultState.Success::class.java)
        val cartItems = (result as ResultState.Success).data
        assertThat(cartItems).hasSize(10)
    }

    @Test
    fun `다음_페이지_버튼을_누르면_페이지_값이_1_증가한다`() {
        viewModel.changeNextPage()

        val currentPage = viewModel.currentPage.getOrAwaitValue()
        assertThat(currentPage).isEqualTo(1)
    }

    @Test
    fun `이전_페이지_버튼을_누르면_페이지_값이_1_감소한다`() {
        viewModel.changeNextPage()
        viewModel.changePreviousPage()

        val currentPage = viewModel.currentPage.getOrAwaitValue()
        assertThat(currentPage).isEqualTo(0)
    }

    @Test
    fun `페이지가_0일_때_이전_페이지_버튼을_누르면_페이지_값이_0으로_유지된다`() {
        viewModel.changePreviousPage()

        val currentPage = viewModel.currentPage.getOrAwaitValue()
        assertThat(currentPage).isEqualTo(0)
    }
}
