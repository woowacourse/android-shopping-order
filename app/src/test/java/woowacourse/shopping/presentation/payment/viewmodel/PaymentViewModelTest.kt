package woowacourse.shopping.presentation.payment.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.fake.FakePaymentNotificationScheduler
import woowacourse.shopping.fake.fakeProduct
import woowacourse.shopping.fake.repository.FakeCouponRepository
import woowacourse.shopping.fake.repository.FakeOrderRepository
import woowacourse.shopping.fake.repository.FakeProductRepository
import woowacourse.shopping.fake.repository.FakeSettingRepository
import woowacourse.shopping.presentation.navigation.OrderItem
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var productRepository: FakeProductRepository
    private lateinit var couponRepository: FakeCouponRepository
    private lateinit var orderRepository: FakeOrderRepository
    private lateinit var settingRepository: FakeSettingRepository
    private lateinit var notificationScheduler: FakePaymentNotificationScheduler
    private lateinit var viewModel: PaymentViewModel

    private val product1 = fakeProduct(1L)
    private val product2 = fakeProduct(2L)
    private val orderItems =
        listOf(
            OrderItem(cartItemId = 100L, productId = 1L, quantity = 2),
            OrderItem(cartItemId = 200L, productId = 2L, quantity = 1),
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        productRepository = FakeProductRepository(listOf(product1, product2))
        couponRepository = FakeCouponRepository()
        orderRepository = FakeOrderRepository()
        settingRepository = FakeSettingRepository()
        notificationScheduler = FakePaymentNotificationScheduler()

        viewModel =
            PaymentViewModel(
                productRepository = productRepository,
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                settingRepository = settingRepository,
                notificationScheduler = notificationScheduler,
            )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onScreenEntered는 주문 금액과 배송비를 초기화한다`() =
        runTest {
            viewModel.onScreenEntered(orderItems)

            val state = viewModel.uiState.value
            assertThat(state.orderAmount).isEqualTo(1250L * 2 + 1250L)
            assertThat(state.deliveryFee).isEqualTo(3000)
            assertThat(state.discountAmount).isEqualTo(0L)
            assertThat(state.totalAmount).isEqualTo(3750L + 3000)
        }

    @Test
    fun `onScreenEntered는 이전 알림 예약을 취소한다`() =
        runTest {
            viewModel.onScreenEntered(orderItems)

            assertThat(notificationScheduler.cancelCallCount).isEqualTo(1)
        }

    @Test
    fun `알림 설정이 켜져있으면 진입 시 알림을 예약한다`() =
        runTest {
            settingRepository.setPaymentPendingNotificationEnabled(true)

            viewModel.onScreenEntered(orderItems)

            assertThat(notificationScheduler.scheduleCallCount).isEqualTo(1)
            assertThat(notificationScheduler.lastScheduledOrderAmount).isEqualTo(3750L)
        }

    @Test
    fun `알림 설정이 꺼져있으면 진입 시 알림을 예약하지 않는다`() =
        runTest {
            settingRepository.setPaymentPendingNotificationEnabled(false)

            viewModel.onScreenEntered(orderItems)

            assertThat(notificationScheduler.scheduleCallCount).isEqualTo(0)
        }

    @Test
    fun `적용 가능한 쿠폰만 불러온다`() =
        runTest {
            couponRepository =
                FakeCouponRepository(
                    coupons =
                        listOf(
                            applicableFixedCoupon(id = 1L, minimumAmount = 100L),
                            applicableFixedCoupon(id = 2L, minimumAmount = 1_000_000L),
                        ),
                )
            recreateViewModel()

            viewModel.onScreenEntered(orderItems)

            val state = viewModel.uiState.value
            assertThat(state.availableCoupons).hasSize(1)
            assertThat(state.availableCoupons.first().id).isEqualTo(1L)
        }

    @Test
    fun `쿠폰을 선택하면 할인 금액과 총액이 갱신된다`() =
        runTest {
            val coupon = applicableFixedCoupon(id = 1L, minimumAmount = 0L, discount = 1000L)
            couponRepository = FakeCouponRepository(coupons = listOf(coupon))
            recreateViewModel()
            viewModel.onScreenEntered(orderItems)

            viewModel.selectCoupon(1L)

            val state = viewModel.uiState.value
            assertThat(state.selectedCouponId).isEqualTo(1L)
            assertThat(state.discountAmount).isEqualTo(1000L)
            assertThat(state.totalAmount).isEqualTo(3750L + 3000 - 1000)
        }

    @Test
    fun `같은 쿠폰을 다시 선택하면 선택이 해제된다`() =
        runTest {
            val coupon = applicableFixedCoupon(id = 1L, minimumAmount = 0L, discount = 1000L)
            couponRepository = FakeCouponRepository(coupons = listOf(coupon))
            recreateViewModel()
            viewModel.onScreenEntered(orderItems)
            viewModel.selectCoupon(1L)

            viewModel.selectCoupon(1L)

            val state = viewModel.uiState.value
            assertThat(state.selectedCouponId).isNull()
            assertThat(state.discountAmount).isEqualTo(0L)
        }

    @Test
    fun `FreeShipping 쿠폰을 선택하면 배송비가 0이 된다`() =
        runTest {
            val coupon =
                Coupon.FreeShipping(
                    id = 1L,
                    description = "무료 배송",
                    expirationDate = LocalDate.now().plusYears(1),
                    minimumAmount = Money(0L),
                )
            couponRepository = FakeCouponRepository(coupons = listOf(coupon))
            recreateViewModel()
            viewModel.onScreenEntered(orderItems)

            viewModel.selectCoupon(1L)

            val state = viewModel.uiState.value
            assertThat(state.deliveryFee).isEqualTo(0)
            assertThat(state.totalAmount).isEqualTo(3750L)
        }

    @Test
    fun `submitOrder는 주문 성공 시 OrderSuccess 이벤트를 발생시킨다`() =
        runTest {
            val events = mutableListOf<PaymentEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiEvents.collect { events.add(it) }
            }
            viewModel.onScreenEntered(orderItems)

            viewModel.submitOrder()

            assertThat(events).anyMatch { it is PaymentEvent.OrderSuccess }
        }

    @Test
    fun `submitOrder는 실패 시 ShowError 이벤트를 발생시킨다`() =
        runTest {
            val events = mutableListOf<PaymentEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiEvents.collect { events.add(it) }
            }
            orderRepository.shouldFail = true
            viewModel.onScreenEntered(orderItems)

            viewModel.submitOrder()

            assertThat(events).anyMatch { it is PaymentEvent.ShowError }
        }

    @Test
    fun `submitOrder는 PaymentItems의 cartItemId들을 OrderRepository에 전달한다`() =
        runTest {
            viewModel.onScreenEntered(orderItems)

            viewModel.submitOrder()

            assertThat(orderRepository.lastRequestedIds).containsExactly(100L, 200L)
        }

    @Test
    fun `submitOrder는 성공 시 알림 예약을 취소한다`() =
        runTest {
            settingRepository.setPaymentPendingNotificationEnabled(true)
            viewModel.onScreenEntered(orderItems)
            val cancelBefore = notificationScheduler.cancelCallCount

            viewModel.submitOrder()

            assertThat(notificationScheduler.cancelCallCount).isGreaterThan(cancelBefore)
        }

    private fun applicableFixedCoupon(
        id: Long,
        minimumAmount: Long,
        discount: Long = 100L,
    ): Coupon.Fixed =
        Coupon.Fixed(
            id = id,
            description = "테스트 쿠폰",
            expirationDate = LocalDate.now().plusYears(1),
            discount = discount,
            minimumAmount = Money(minimumAmount),
        )

    private fun recreateViewModel() {
        viewModel =
            PaymentViewModel(
                productRepository = productRepository,
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                settingRepository = settingRepository,
                notificationScheduler = notificationScheduler,
            )
    }
}
