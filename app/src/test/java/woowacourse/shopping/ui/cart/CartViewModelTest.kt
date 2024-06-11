package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.FakeRecentProductDao
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    private lateinit var viewModel: CartViewModel
    private lateinit var productRepository: ProductRepositoryImpl
    private val recentProductRepository = RecentProductRepositoryImpl.get(FakeRecentProductDao)
    private lateinit var cartRepository: CartRepositoryImpl

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        productRepository = mockk<ProductRepositoryImpl>()
        cartRepository = mockk<CartRepositoryImpl>()
        coEvery { cartRepository.getAllCartItems() } returns Result.Success(CART_STUB)
        viewModel = CartViewModel(productRepository, cartRepository, recentProductRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `장바구니에 아이템이 4개 있을 때, 삭제 버튼을 누르면 아이템이 3개가 된다`() {
        // when
        val before = viewModel.cart.getOrAwaitValue()
        assertThat(before.cartItems).hasSize(4)
        coEvery { cartRepository.deleteCartItem(DELETED_CART_ID) } returns Result.Success(Unit)
        coEvery { cartRepository.getAllCartItems() } returns Result.Success(CART_STUB.filterNot { it.id == DELETED_CART_ID })

        // given
        viewModel.removeCartItem(DELETED_CART_ID)

        // then
        val actual = viewModel.cart.getOrAwaitValue()
        assertThat(actual.cartItems.size).isEqualTo(3)
    }

    companion object {
        val CART_STUB =
            (0..3).toList().map {
                CartWithProduct(
                    it.toLong(),
                    Product(it.toLong(), "", "", (1_00 * it), ""),
                    Quantity(1),
                )
            }
        const val DELETED_CART_ID = 0L
    }
}
