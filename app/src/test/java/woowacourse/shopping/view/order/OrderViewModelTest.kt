package woowacourse.shopping.view.order

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.order.repository.OrderRepository
import woowacourse.shopping.fixture.TestFixtures.BONUS_COUPONS
import woowacourse.shopping.fixture.TestFixtures.PRICE_DISCOUNT_COUPONS
import woowacourse.shopping.fixture.TestFixtures.PRODUCTS_TO_ORDER_BONUS_COUPON
import woowacourse.shopping.fixture.TestFixtures.PRODUCTS_TO_ORDER_PRICE_DISCOUNT
import woowacourse.shopping.view.common.CoroutinesTestExtension
import woowacourse.shopping.view.common.InstantTaskExecutorExtension
import woowacourse.shopping.view.common.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var viewModel: OrderViewModel
    private lateinit var orderRepository: OrderRepository
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setUp() {
        couponRepository = mockk()
        orderRepository = mockk()

        viewModel =
            OrderViewModel(PRODUCTS_TO_ORDER_BONUS_COUPON, couponRepository, orderRepository)
    }

    @Test
    fun `쿠폰 조회 성공 시 사용가능한 쿠폰을 가져온다`() =
        runTest {
            // given:
            coEvery { couponRepository.getCoupons() } returns Result.success(BONUS_COUPONS)

            // when:
            // then:
            val actual: List<CouponItem> = viewModel.coupons.getOrAwaitValue()
            val actualCoupons = actual.map { it.origin }
            assertThat(actualCoupons).containsAll(BONUS_COUPONS)
        }

    @Test
    fun `주문 생성 성공 시 CREATE_ORDER_SUCCESS 이벤트를 발생한다`() =
        runTest {
            // given:
            coEvery { orderRepository.placeOrder(any()) } returns Result.success(Unit)

            // when:
            viewModel.createOrder()

            // then:
            assertThat(viewModel.event.getValue())
                .isEqualTo(OrderEvent.CREATE_ORDER_SUCCESS)
        }

    @Test
    fun `주문 생성 실패 시 CREATE_ORDER_FAILURE 이벤트를 발생한다`() =
        runTest {
            // given:
            coEvery { orderRepository.placeOrder(any()) } returns Result.failure(Exception())

            // when:
            viewModel.createOrder()

            // then:
            assertThat(viewModel.event.getValue())
                .isEqualTo(OrderEvent.CREATE_ORDER_FAILURE)
        }

    @Test
    fun `쿠폰 선택하면 applyingCoupon과 isSelected 상태가 업데이트됨`() =
        runTest {
            // given:
            coEvery { couponRepository.getCoupons() } returns Result.success(PRICE_DISCOUNT_COUPONS)

            viewModel =
                OrderViewModel(
                    PRODUCTS_TO_ORDER_PRICE_DISCOUNT,
                    couponRepository,
                    orderRepository,
                )

            // when:
            val couponId =
                viewModel.coupons
                    .getOrAwaitValue()
                    .first()
                    .id
            viewModel.updateApplyingCoupon(couponId)

            // then:
            assertThat(viewModel.applyingCoupon.getOrAwaitValue().id).isEqualTo(couponId)
            assertThat(
                viewModel.coupons
                    .getOrAwaitValue()
                    .first()
                    .isSelected,
            ).isTrue()
        }
}
