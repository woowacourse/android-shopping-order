package woowacourse.shopping.view.order

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.FakeCouponRepository
import woowacourse.shopping.data.repository.FakeOrderRepository
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.CoroutinesTestExtension
import woowacourse.shopping.utils.InstantTaskExecutorExtension
import woowacourse.shopping.utils.getFixtureCartItems
import woowacourse.shopping.utils.getOrAwaitValue
import java.time.LocalDate

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var couponRepository: CouponRepository
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setUp() {
        couponRepository = FakeCouponRepository()
        orderRepository = FakeOrderRepository()
        orderViewModel =
            OrderViewModel(
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                cartItems = getFixtureCartItems(5, 50000),
            )
    }

    @Test
    fun `쿠폰을 사용하지 않은 채로 주문을 진행할 수 있다`() =
        runTest {
            // when
            orderViewModel.makeOrder()

            // then
            val actualEvent = orderViewModel.orderUiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertThat(actualEvent).isEqualTo(OrderUiEvent.NavigateBackToHome)
        }

    @Test
    fun `사용할 쿠폰을 선택할 수 있다`() {
        // when
        orderViewModel.changeCouponSelection(isSelected = true, couponId = 1)

        // then
        val actual = orderViewModel.orderUiState.getOrAwaitValue()
        assertThat(actual.selectedCoupon).isEqualTo(
            Coupon.Fixed(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.parse("2024-11-30"),
                discount = 5000,
                minimumAmount = 100000,
                discountType = "fixed",
            ),
        )
    }

    @Test
    fun `사용할 쿠폰을 선택하면 할인 금액이 적용된다`() {
        // when
        orderViewModel.changeCouponSelection(isSelected = true, couponId = 1)

        // then
        val actual = orderViewModel.orderUiState.getOrAwaitValue()
        assertThat(actual.discount).isEqualTo(5000)
    }

    @Test
    fun `선택한 쿠폰을 해제할 수 있다`() {
        // when
        orderViewModel.changeCouponSelection(isSelected = true, couponId = 1)
        orderViewModel.changeCouponSelection(isSelected = false, couponId = 1)

        // then
        val actual = orderViewModel.orderUiState.getOrAwaitValue()
        assertThat(actual.selectedCoupon).isEqualTo(null)
    }

    @Test
    fun `쿠폰을 선택한 상태에서 다른 쿠폰을 선택하면 최근 선택된 것만 적용된다`() {
        // when
        orderViewModel.changeCouponSelection(isSelected = true, couponId = 1)
        orderViewModel.changeCouponSelection(isSelected = true, couponId = 3)

        // then
        val actual = orderViewModel.orderUiState.getOrAwaitValue()
        assertThat(actual.selectedCoupon).isEqualTo(
            Coupon.FreeShipping(
                id = 3,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = LocalDate.parse("2024-08-31"),
                minimumAmount = 50000,
                discountType = "freeShipping",
            ),
        )
    }
}
