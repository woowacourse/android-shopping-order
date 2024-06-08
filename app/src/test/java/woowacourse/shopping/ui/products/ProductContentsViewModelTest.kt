package woowacourse.shopping.ui.products

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.data.cart.Cart
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProduct
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
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
        coEvery { productRepository.getProducts(0, 20).getOrThrow() } returns
            PRODUCT_STUB.subList(
                0,
                20,
            )
        viewModel =
            ProductContentsViewModel(productRepository, recentProductRepository, cartRepository)
    }

    @Test
    fun `상품을 가져올 때, 20개씩 가져온다`() {
        // given
        coEvery { productRepository.getProducts(1, 20).getOrThrow() } returns
            PRODUCT_STUB.subList(
                20,
                40,
            )

        // when
        viewModel.loadProducts()
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        // then
        assertThat(actual.productWithQuantities).hasSize(40)
    }

    @Test
    fun `장바구니에 상품을 추가하면, 해당 상품의 quantity가 1이 된다`() {
        // given
        coEvery { cartRepository.postCartItems(0L, 1).getOrThrow() } returns mockk()
        coEvery { cartRepository.getAllCartItems().getOrThrow() } returns
            listOf(
                Cart(
                    0L,
                    0L,
                    Quantity(1),
                ),
            )

        // when
        viewModel.addCart(0L)
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        val actualProduct = (actual.productWithQuantities as List<ProductWithQuantityUiModel>)

        // then
        assertThat(actualProduct.first { it.product.id == 0L }.quantity).isEqualTo(1)
    }

    @Test
    fun `상품을 클릭하면 해당 상품의 id를 저장한다`() {
        // given

        // when
        viewModel.itemClickListener(0L)
        val actual = viewModel.productDetailId.getValue()

        // then
        assertThat(actual).isEqualTo(0L)
    }

    @Test
    fun `최근 본 상품을 불러온다`() {
        // given
        coEvery { recentProductRepository.findAll().getOrThrow() } returns listOf(RECENT_PRODUCT_STUB)
        coEvery { productRepository.find(0L).getOrThrow() } returns PRODUCT_STUB[0]

        // when
        viewModel.loadRecentProducts()
        val actual = viewModel.recentProducts.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(listOf(PRODUCT_STUB[0]))
    }

    companion object {
        val PRODUCT_STUB = (0..60).toList().map { Product(it.toLong(), "", "", 0, "") }
        val RECENT_PRODUCT_STUB =
            RecentProduct(
                0L,
                0L,
                LocalDateTime.of(2024, 3, 28, 21, 0),
            )
    }
}
