package woowacourse.shopping.coupon

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartItemsBySize
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.fake.FakeCartRepository
import woowacourse.shopping.fake.FakeCouponRepository
import woowacourse.shopping.fake.FakeOrderRepository
import woowacourse.shopping.fixedCoupon
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.toCouponUiModels
import woowacourse.shopping.ui.coupon.CouponViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CouponViewModelTest {
    private lateinit var viewModel: CouponViewModel

    private lateinit var cartRepository: CartRepository
    private lateinit var couponRepository: CouponRepository
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setUp() {
        orderRepository = FakeOrderRepository()
    }

    @Test
    fun `적용할 수 있는 쿠폰을 로드한다`(): Unit =
        runBlocking {
            // given
            val cartItems = cartItemsBySize(5)
            val selectedCartItemIds = cartItems.map { it.id }
            val coupons = List(5) { fixedCoupon(id = it, discount = 1_000) }
            setUpViewModel(selectedCartItemIds, cartItems, coupons)

            // when

            // then
            val actual = viewModel.couponUiModels.getOrAwaitValue().uiModels
            assertThat(actual).hasSize(5)
            assertThat(actual).isEqualTo(coupons.toCouponUiModels())
        }

    @Test
    fun `적용할 수 있는 쿠폰이 없는 경우 쿠폰 목록이 비어있다`(): Unit =
        runBlocking {
            // given
            val cartItems = cartItemsBySize(5)
            val selectedCartItemIds = cartItems.map { it.id }
            setUpViewModel(selectedCartItemIds, cartItems, listOf())

            // when

            // then
            val actual = viewModel.couponUiModels.getOrAwaitValue().uiModels
            assertThat(actual).isEmpty()
            assertThat(viewModel.isEmptyCoupon.getOrAwaitValue()).isTrue
        }

    @Test
    fun `장바구니 상품을 결제한다`(): Unit =
        runBlocking {
            // given
            val cartItems = cartItemsBySize(5)
            val selectedCartItemIds = cartItems.map { it.id }
            setUpViewModel(selectedCartItemIds, cartItems, listOf())

            // when
            viewModel.createOrder()

            // then
            assertThat(viewModel.isSuccessCreateOrder.isInitialized).isTrue
        }

    private fun setUpViewModel(
        selectedCartItemIds: List<Int>,
        savedCartItems: List<CartItem> = emptyList(),
        savedCoupons: List<Coupon> = emptyList(),
    ) {
        cartRepository = FakeCartRepository(savedCartItems)
        couponRepository = FakeCouponRepository(savedCoupons)

        viewModel =
            CouponViewModel(
                selectedCartItemIds,
                cartRepository,
                couponRepository,
                orderRepository,
            )
    }
}
