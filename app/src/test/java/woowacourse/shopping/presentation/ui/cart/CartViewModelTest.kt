package woowacourse.shopping.presentation.ui.cart

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecommendRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.CartModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class CartViewModelTest {
    @MockK
    private lateinit var recommendRepository: RecommendRepository

    @MockK
    private lateinit var cartRepository: CartRepository

    private lateinit var viewModel: CartViewModel

    @Test
    fun `모든 장바구니 아이템들을 불러와 주문 목록에 포함시킨다`() =
        runTest {
            val cartItems = listOf(Cart(0L), Cart(1L))
            coEvery { cartRepository.load(any(), any()) } returns Result.success(cartItems)

            viewModel = CartViewModel(cartRepository, recommendRepository, 2)

            val actual = viewModel.cartItems.getOrAwaitValue()
            val expected = listOf(CartModel(0L, isChecked = true), CartModel(1L, isChecked = true))
            assertThat(actual).isEqualTo(UiState.Success(expected))
        }

    @Test
    fun `첫 번째 장바구니 아이템을 삭제하면, 선택된 장바구니 아이템은 장바구니 목록에서 제거 된다`() =
        runTest {
            val cartItems = listOf(Cart(0L), Cart(1L))
            coEvery { cartRepository.load(any(), any()) } returns Result.success(cartItems)
            coEvery { cartRepository.deleteCartItem(0L) } returns Result.success(Unit)

            viewModel = CartViewModel(cartRepository, recommendRepository, 2)
            viewModel.deleteCartItem(0L)

            val actual = viewModel.cartItems.getOrAwaitValue()
            val expected = listOf(CartModel(1L, isChecked = true))
            assertThat(actual).isEqualTo(UiState.Success(expected))
        }

    @Test
    fun `수량이 2개 씩인 장바구니 아이템 전체 2개를 모두 선택한 경우, 선택된 아이템들의 총 개수는 4이다`() =
        runTest {
            val cartItems = listOf(Cart(0L, quantity = 2), Cart(1L, quantity = 2))
            coEvery { cartRepository.load(any(), any()) } returns Result.success(cartItems)

            viewModel = CartViewModel(cartRepository, recommendRepository, 2)

            val actual = viewModel.totalCount.getOrAwaitValue()
            val expected = 4
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `수량이 2개 씩인 장바구니 아이템 전체 2개 중 하나를 선택 해제할 경우, 선택된 아이템들의 총 개수는 2이다`() =
        runTest {
            val cartItems = listOf(Cart(0L, quantity = 2), Cart(1L, quantity = 2))
            coEvery { cartRepository.load(any(), any()) } returns Result.success(cartItems)

            viewModel = CartViewModel(cartRepository, recommendRepository, 2)
            viewModel.selectCartItem(0L)

            val actual = viewModel.totalCount.getOrAwaitValue()
            val expected = 2
            assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `한 번에 모든 장바구니 아이템을 주문 목록 포함을 해제시킬 수 있다`() =
        runTest {
            val cartItems = listOf(Cart(0L), Cart(1L))
            coEvery { cartRepository.load(any(), any()) } returns Result.success(cartItems)
            coEvery { cartRepository.deleteCartItem(0L) } returns Result.success(Unit)

            viewModel = CartViewModel(cartRepository, recommendRepository, 2)
            viewModel.selectAllCartItems(isChecked = false)

            val actual = viewModel.isAllCartItemsSelected.getOrAwaitValue()
            val expected = false
            assertThat(actual).isEqualTo(expected)
        }
}

private fun Cart(
    id: Long,
    productPrice: Int = 1_000,
    quantity: Int = 1,
): Cart {
    return Cart(cartId = id, product = Product(id, productPrice), quantity = quantity)
}

private fun Product(
    id: Long,
    price: Int,
): Product {
    return Product(id, "상품 샘플", "", price.toLong(), "")
}

private fun CartModel(
    id: Long,
    productPrice: Int = 1_000,
    quantity: Int = 1,
    isChecked: Boolean,
): CartModel {
    return CartModel(
        cartId = id,
        productId = id,
        name = "상품 샘플",
        imageUrl = "",
        price = productPrice.toLong(),
        quantity = quantity,
        isChecked = isChecked,
        calculatedPrice = (productPrice * quantity),
    )
}
