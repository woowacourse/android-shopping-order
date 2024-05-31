package woowacourse.shopping.ui.products

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.cart.Cart
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel

@ExtendWith(InstantTaskExecutorExtension::class)
class ProductContentsViewModelTest {
    private lateinit var viewModel: ProductContentsViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        productRepository = mockk<ProductRepository>()
        recentProductRepository = mockk<RecentProductRepository>()
        cartRepository = mockk<CartRepository>()
        every { productRepository.getProducts(0, 20).getOrThrow() } returns PRODUCT_STUB.subList(0, 20)
        viewModel =
            ProductContentsViewModel(productRepository, recentProductRepository, cartRepository)
    }

    @Test
    fun `상품을 가져올 때, 20개씩 가져온다`() {
        // given
        every { productRepository.getProducts(1, 20).getOrThrow() } returns PRODUCT_STUB.subList(20, 40)

        // when
        viewModel.loadProducts()
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        // then
        assertThat(actual.productWithQuantities).hasSize(40)
    }

    @Test
    fun `장바구니에 상품을 추가하면, 해당 상품의 quantity가 1이 된다`() {
        // given
        every { cartRepository.addProductToCart(0, 1) }
        every { cartRepository.getAllCartItems().getOrThrow() } returns listOf(Cart(0L, 0L, Quantity(1)))

        // when
        viewModel.addCart(0)
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        val actualProduct = (actual as List<ProductWithQuantityUiModel>)

        // then
        assertThat(actualProduct.first { it.product.id == 0L }.quantity).isEqualTo(1)
    }

    companion object {
        val PRODUCT_STUB = (0..60).toList().map { Product(it.toLong(), "", "", 0, "") }
    }
}
