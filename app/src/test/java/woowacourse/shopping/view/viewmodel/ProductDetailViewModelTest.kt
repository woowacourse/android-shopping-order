package woowacourse.shopping.view.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.TestFixture.getOrAwaitValue
import woowacourse.shopping.TestProductRepository
import woowacourse.shopping.TestRecentlyProductRepository
import woowacourse.shopping.TestShoppingCartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.view.detail.ProductDetailViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductDetailViewModelTest {
    private lateinit var productRepository: ProductRepository
    private lateinit var shoppingCartRepository: ShoppingCartRepository
    private lateinit var viewModel: ProductDetailViewModel

    @BeforeEach
    fun setUp() {
        productRepository = TestProductRepository()
        shoppingCartRepository = TestShoppingCartRepository()
        val recentlyProductRepository = TestRecentlyProductRepository()
        viewModel =
            ProductDetailViewModel(
                productRepository = productRepository,
                shoppingCartRepository = shoppingCartRepository,
                recentlyProductRepository = recentlyProductRepository,
            )
    }

    @Test
    fun `상품아이디로_상품을_요청하면_아이디와_일치하는_상품_목록을_반환해야_한다`() {
        viewModel.loadProductItem(0)
        val actual = viewModel.product.getOrAwaitValue()
        val expected = (productRepository as TestProductRepository).products[0]

        assertThat(actual).isEqualTo(expected)
    }
}
