package woowacourse.shopping.ui.detail

import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.ui.FakeRecentProductDao
import woowacourse.shopping.ui.detail.viewmodel.ProductDetailViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var productRepository: ProductRepositoryImpl
    private val recentProductRepository = RecentProductRepositoryImpl.get(FakeRecentProductDao)
    private lateinit var cartRepository: CartRepositoryImpl

//    @BeforeEach
//    fun setUp() {
//        productRepository = mockk<ProductRepositoryImpl>()
//        cartRepository = mockk<CartRepositoryImpl>()
//        every { productRepository.find(1L) } returns PRODUCT_STUB
//        viewModel =
//            ProductDetailViewModel(1L, productRepository, recentProductRepository, cartRepository)
//    }
//
//    @Test
//    fun `선택한 상품이 불러와진다`() {
//        // given
//        every { productRepository.find(1L) } returns PRODUCT_STUB
//
//        // when
//        viewModel.loadProduct()
//        val actual = viewModel.productWithQuantity.getOrAwaitValue()
//
//        // then
//        assertThat(actual.product.name).isEqualTo(PRODUCT_STUB.name)
//    }
//
//    @Test
//    fun `상품의 수량이 증가한다`() {
//        // given
//
//        // when
//        viewModel.plusCount(1L)
//        val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
//
//        // then
//        assertThat(actual).isEqualTo(1)
//    }
//
//    @Test
//    fun `상품의 수량이 감소한다`() {
//        // given
//        viewModel.plusCount(1L)
//        viewModel.plusCount(1L)
//        viewModel.plusCount(1L)
//
//        // when
//        viewModel.minusCount(1L)
//        val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
//
//        // then
//        assertThat(actual).isEqualTo(2)
//    }
//
//    companion object {
//        private val PRODUCT_STUB = Product(imageUrl = "", name = "TEST", price = 0, category = "")
//    }
}
