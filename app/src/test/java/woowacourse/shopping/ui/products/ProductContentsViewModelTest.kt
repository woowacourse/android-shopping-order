package woowacourse.shopping.ui.products

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
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductContentsViewModelTest {
    private lateinit var viewModel: ProductContentsViewModel

    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        productRepository = mockk<ProductRepository>()
        recentProductRepository = mockk<RecentProductRepository>()
        cartRepository = mockk<CartRepository>()
        coEvery {
            productRepository.getAllProducts(
                0,
                20,
            )
        } returns Result.Success(PRODUCT_STUB.subList(0, 20))
        viewModel =
            ProductContentsViewModel(productRepository, recentProductRepository, cartRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `상품을 가져올 때, 20개씩 가져온다`() {
        // when
        coEvery {
            productRepository.getAllProducts(
                1,
                20,
            )
        } returns Result.Success(PRODUCT_STUB.subList(20, 40))

        // given
        viewModel.loadProducts()
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        // then
        assertThat(actual.productWithQuantities).hasSize(40)
    }

    @Test
    fun `장바구니에 상품을 추가하면, 해당 상품의 quantity가 1이 된다`() {
        // when
        coEvery { cartRepository.postCartItems(0, 1) } returns Result.Success(Unit)
        coEvery { cartRepository.getAllCartItems() } returns
            Result.Success(
                listOf(
                    CartWithProduct(0L, PRODUCT_STUB.first(), Quantity(1)),
                ),
            )

        // given
        viewModel.addCart(0)
        val actual = viewModel.productWithQuantity.getOrAwaitValue()

        val actualProduct = (actual.productWithQuantities as List<ProductWithQuantityUiModel>)

        // then
        assertThat(actualProduct.first { it.product.id == 0L }.quantity).isEqualTo(1)
    }

    companion object {
        val PRODUCT_STUB = (0..60).toList().map { Product(it.toLong(), "", "", 0, "") }
    }
}
