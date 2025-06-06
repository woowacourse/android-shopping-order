package woowacourse.shopping.viewModel.shoppingCart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.fixture.ALL_TOGGLED
import woowacourse.shopping.fixture.SHOPPING_CARTS1
import woowacourse.shopping.fixture.SHOPPING_CART_DECREASED
import woowacourse.shopping.fixture.SHOPPING_CART_INCREASED
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCT1
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCT2
import woowacourse.shopping.fixture.SHOPPING_CART_REMOVED
import woowacourse.shopping.view.shoppingCart.OrderEvent
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem
import woowacourse.shopping.view.shoppingCart.ShoppingCartViewModel
import woowacourse.shopping.viewModel.common.CoroutinesTestExtension
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension
import woowacourse.shopping.viewModel.common.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@Suppress("FunctionName")
class ShoppingCartViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        shoppingCartRepository = mockk()
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CARTS1)

        coEvery { shoppingCartRepository.updateQuantity(any(), eq(1)) } returns
            Result.success(SHOPPING_CART_PRODUCT1.copy(quantity = 1))

        coEvery { shoppingCartRepository.updateQuantity(any(), eq(3)) } returns
            Result.success(SHOPPING_CART_PRODUCT1.copy(quantity = 3))

        coEvery { shoppingCartRepository.updateQuantity(any(), eq(0)) } returns
            Result.success(SHOPPING_CART_PRODUCT2.copy(quantity = 0))

        viewModel = ShoppingCartViewModel(shoppingCartRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `처음 로딩하면 5개의 장바구니 아이템을 가져온다`() {
        // given
        val expected =
            SHOPPING_CARTS1.shoppingCartItems.map {
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = it,
                    isChecked = false,
                )
            }
        // when
        viewModel =
            ShoppingCartViewModel(
                shoppingCartRepository,
            )

        // then
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .isEqualTo(expected)
            .hasSize(5)
    }

    @Test
    fun `다음 페이지의 장바구니 아이템을 가져올 수 있다`() =
        runTest {
            // given
            val expected =
                (SHOPPING_CARTS1.shoppingCartItems + SHOPPING_CARTS1.shoppingCartItems).map {
                    ShoppingCartItem.ShoppingCartProductItem(
                        shoppingCartProduct = it,
                        isChecked = false,
                    )
                }

            // when
            viewModel.plusPage()

            // then
            assertThat(viewModel.shoppingCart.getOrAwaitValue())
                .isEqualTo(expected)
                .hasSize(10)

            Dispatchers.resetMain()
        }

    @Test
    fun `특정 상품의 수량을 늘릴 수 있다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                isChecked = false,
            )
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CART_INCREASED)

        // when
        viewModel.increaseQuantity(shoppingCartProductItem)

        // then
        coVerify { shoppingCartRepository.updateQuantity(1, 3) }
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .contains(
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = SHOPPING_CART_PRODUCT1.copy(quantity = 3),
                    isChecked = false,
                ),
            )
    }

    @Test
    fun `특정 상품의 수량을 줄일 수 있다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                isChecked = false,
            )
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CART_DECREASED)

        // when
        viewModel.decreaseQuantity(shoppingCartProductItem)

        // then
        coVerify { shoppingCartRepository.updateQuantity(1, 1) }
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .contains(
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = SHOPPING_CART_PRODUCT1.copy(quantity = 1),
                    isChecked = false,
                ),
            )
    }

    @Test
    fun `수량이 1개일 때 수량을 줄이면 아이템이 삭제된다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT2,
                isChecked = false,
            )

        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CART_REMOVED)

        // when
        viewModel.decreaseQuantity(shoppingCartProductItem)

        // then
        coVerify { shoppingCartRepository.updateQuantity(2, 0) }
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .doesNotContain(
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = SHOPPING_CART_PRODUCT2,
                    isChecked = false,
                ),
            )
    }

    @Test
    fun `상품을 삭제할 수 있다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT2,
                isChecked = false,
            )

        coEvery { shoppingCartRepository.remove(2) } returns Result.success(Unit)

        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CART_REMOVED)

        // when
        viewModel.removeShoppingCartProduct(shoppingCartProductItem)

        // then
        coVerify { shoppingCartRepository.remove(2) }
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .doesNotContain(
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = SHOPPING_CART_PRODUCT2,
                    isChecked = false,
                ),
            )
    }

    @Test
    fun `상품을 다시 로딩하면 상품이 초기화하고 다시 불러온다`() {
        // given
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.success(SHOPPING_CARTS1)

        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        // when
        viewModel.reload()

        // then

        assertThat(viewModel.shoppingCart.getOrAwaitValue()).isEmpty()

        dispatcher.scheduler.runCurrent()
        dispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .isEqualTo(
                SHOPPING_CARTS1.shoppingCartItems.map {
                    ShoppingCartItem.ShoppingCartProductItem(
                        shoppingCartProduct = it,
                        isChecked = false,
                    )
                },
            )

        Dispatchers.resetMain()
    }

    @Test
    fun `특정 장바구니 아이템의 선택 상태를 반전시킬 수 있다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                isChecked = false,
            )

        // when
        viewModel.toggleShoppingCartProduct(shoppingCartProductItem)

        // then
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .contains(
                ShoppingCartItem.ShoppingCartProductItem(
                    shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                    isChecked = true,
                ),
            )
    }

    @Test
    fun `모든 장바구니 아이템을 선택시킬 수 있다`() {
        // given
        // 모든 상품은 선택되지 않고 있습니다

        // when
        viewModel.selectAllShoppingCartProducts()

        // then
        assertThat(viewModel.shoppingCart.getOrAwaitValue())
            .isEqualTo(ALL_TOGGLED)
    }

    @Test
    fun `상품을 주문할 수 있다면 주문 가능 이벤트를 발생시킨다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                isChecked = false,
            )
        viewModel.toggleShoppingCartProduct(shoppingCartProductItem)
        viewModel.orderBarState.getOrAwaitValue()

        // when
        viewModel.checkoutIfPossible()

        // then
        assertThat(viewModel.orderEvent.getValue())
            .isEqualTo(OrderEvent.PROCEED)
    }

    @Test
    fun `선택된 아이템이 변경되면 하단 주문 바의 상태도 변경된다`() {
        // given
        val shoppingCartProductItem =
            ShoppingCartItem.ShoppingCartProductItem(
                shoppingCartProduct = SHOPPING_CART_PRODUCT1,
                isChecked = false,
            )

        // when
        viewModel.toggleShoppingCartProduct(shoppingCartProductItem)

        // then
        assertThat(viewModel.orderBarState.getOrAwaitValue().isOrderEnabled)
            .isTrue()

        assertThat(viewModel.orderBarState.getOrAwaitValue().totalPrice)
            .isEqualTo(SHOPPING_CART_PRODUCT1.price)

        assertThat(viewModel.orderBarState.getOrAwaitValue().totalQuantity)
            .isEqualTo(SHOPPING_CART_PRODUCT1.quantity)
    }
}
