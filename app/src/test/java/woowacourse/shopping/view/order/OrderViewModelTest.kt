package woowacourse.shopping.view.order

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.TestFixture.bogoCoupon
import woowacourse.shopping.TestFixture.cartItem0
import woowacourse.shopping.TestFixture.fixed5000DiscountCoupon
import woowacourse.shopping.TestFixture.freeShippingCoupon
import woowacourse.shopping.TestFixture.getOrAwaitValue
import woowacourse.shopping.TestFixture.notNowTimeBasedDiscountCoupon
import woowacourse.shopping.TestFixture.nowTimeBasedDiscountCoupon
import woowacourse.shopping.TestFixture.shoppingCartItemsNotBogo
import woowacourse.shopping.TestFixture.shoppingCartItemsTotal126000
import woowacourse.shopping.TestFixture.shoppingCartItemsTotal24000
import woowacourse.shopping.TestFixture.shoppingCartItemsTotal90000
import woowacourse.shopping.TestFixture.shoppingCartItemsTripleCartItem0
import woowacourse.shopping.TestOrderRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.order.model.CouponUiModelMapper.toUiModel
import woowacourse.shopping.view.order.state.OrderUiState

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var viewModel: OrderViewModel

    @BeforeEach
    fun setUp() {
        orderRepository = TestOrderRepository()
        viewModel = OrderViewModel(orderRepository)
    }

    @Test
    fun `장바구니에 담긴 상품을 주문하면 주문이 완료되어야 한다`() {
        // given
        val before = viewModel.orderUiState.getOrAwaitValue()
        assertThat(before).isInstanceOf(OrderUiState.Idle::class.java)

        // when
        viewModel.orderItems()

        // then
        val result = viewModel.orderUiState.getOrAwaitValue()
        assertThat(result).isInstanceOf(OrderUiState.Success::class.java)
    }

    @Test
    fun `10만원 이상 구매시 5000원 할인 쿠폰이 적용된다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal126000)
        assertThat(viewModel.totalPrice.getOrAwaitValue()).isEqualTo(126000)

        // when
        viewModel.applyCoupon(fixed5000DiscountCoupon.toUiModel())

        // then
        val discount = viewModel.couponDiscount.getOrAwaitValue()
        assertThat(discount).isEqualTo(5000)
    }

    @Test
    fun `10만원 미만 구매시 5000원 할인 쿠폰이 적용되지 않는다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal90000)
        assertThat(viewModel.totalPrice.getOrAwaitValue()).isEqualTo(90000)

        // when
        viewModel.applyCoupon(fixed5000DiscountCoupon.toUiModel())

        // then
        val discount = viewModel.couponDiscount.getOrAwaitValue()
        assertThat(discount).isEqualTo(0)
    }

    @Test
    fun `3개 같은 상품을 담을 시 1개는 무료 쿠폰이 적용된다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTripleCartItem0)

        // when
        viewModel.applyCoupon(bogoCoupon.toUiModel())
        val discount = viewModel.couponDiscount.getOrAwaitValue()

        // then
        assertThat(discount).isEqualTo(cartItem0.product.price)
    }

    @Test
    fun `3개 같은 상품이 없을 시 쿠폰은 적용되지 않는다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsNotBogo)

        // when
        viewModel.applyCoupon(bogoCoupon.toUiModel())
        val discount = viewModel.couponDiscount.getOrAwaitValue()

        // then
        assertThat(discount).isEqualTo(0)
    }

    @Test
    fun `50000원 이상 구매시 무료배송 쿠폰이 적용되면 배송비가 0원이 된다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal126000)
        assertThat(viewModel.deliveryFee.getOrAwaitValue()).isEqualTo(3000)

        // when
        viewModel.applyCoupon(freeShippingCoupon.toUiModel())

        // then
        val deliveryFee = viewModel.deliveryFee.getOrAwaitValue()
        assertThat(deliveryFee).isEqualTo(0)
    }

    @Test
    fun `50000원 미만 구매시 무료배송 쿠폰이 적용되지 않으면 배송비가 3000원이 된다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal24000)
        assertThat(viewModel.deliveryFee.getOrAwaitValue()).isEqualTo(3000)

        // when
        viewModel.applyCoupon(freeShippingCoupon.toUiModel())

        // then
        val deliveryFee = viewModel.deliveryFee.getOrAwaitValue()
        assertThat(deliveryFee).isEqualTo(3000)
    }

    @Test
    fun `timeBasedDiscountCoupon의 시간대면 쿠폰이 적용된다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal90000)
        assertThat(viewModel.totalPrice.getOrAwaitValue()).isEqualTo(90000)

        // when
        viewModel.applyCoupon(nowTimeBasedDiscountCoupon.toUiModel())

        // then
        val discount = viewModel.couponDiscount.getOrAwaitValue()
        assertThat(discount).isEqualTo(27000)
    }

    @Test
    fun `timeBasedDiscountCoupon의 시간대가 아니면 쿠폰이 적용되지 않는다`() {
        // given
        viewModel.saveCheckedShoppingCarts(shoppingCartItemsTotal90000)
        assertThat(viewModel.totalPrice.getOrAwaitValue()).isEqualTo(90000)

        // when
        viewModel.applyCoupon(notNowTimeBasedDiscountCoupon.toUiModel())

        // then
        val discount = viewModel.couponDiscount.getOrAwaitValue()
        assertThat(discount).isEqualTo(0)
    }
}
