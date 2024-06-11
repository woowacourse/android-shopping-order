package woowacourse.shopping.ui.detail

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.FakeRecentProductDao
import woowacourse.shopping.ui.detail.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var productRepository: ProductRepositoryImpl
    private val recentProductRepository = RecentProductRepositoryImpl.get(FakeRecentProductDao)
    private lateinit var cartRepository: CartRepositoryImpl

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        productRepository = mockk<ProductRepositoryImpl>()
        cartRepository = mockk<CartRepositoryImpl>()
        coEvery { productRepository.getProductById(PRODUCT_ID) } returns Result.Success(PRODUCT_STUB)
        viewModel =
            ProductDetailViewModel(PRODUCT_ID, productRepository, recentProductRepository, cartRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `뷰모델을 생성하면, 초기 데이터가 불러와진다`() {
        // given
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        // then
        assertThat(actual.product.name).isEqualTo(PRODUCT_STUB.name)
    }

    @Test
    fun `상품의 수량이 0일 때, 증가 시키면 상품의 수량이 1이 된다`() {
        // when
        val before = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
        assertThat(before).isEqualTo(0)

        // given
        viewModel.plusCount(PRODUCT_ID)

        // then
        val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun `상품의 수량이 2일 때, 감소시키면 상품의 수량이 1이 된다`() {
        // when
        viewModel.plusCount(PRODUCT_ID)
        viewModel.plusCount(PRODUCT_ID)
        val before = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
        assertThat(before).isEqualTo(2)

        // given
        viewModel.minusCount(PRODUCT_ID)

        // then
        val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
        assertThat(actual).isEqualTo(1)
    }

    companion object {
        private const val PRODUCT_ID = 1L
        private val PRODUCT_STUB = Product(imageUrl = "", name = "TEST", price = 0, category = "")
    }
}
