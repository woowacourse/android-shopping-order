package woowacourse.shopping.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.ui.payment.PaymentEvent
import woowacourse.shopping.ui.payment.PaymentViewModel
import woowacourse.shopping.viewmodel.fakes.FakeAlarmScheduler
import woowacourse.shopping.viewmodel.fakes.FakeCartRepository
import woowacourse.shopping.viewmodel.fakes.FakeCouponRepository
import woowacourse.shopping.viewmodel.fakes.FakeNotificationSettingStorage
import woowacourse.shopping.viewmodel.fakes.FakeOrderRepository
import woowacourse.shopping.viewmodel.fakes.FakeOutstandingProductRepository
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var cartRepository: FakeCartRepository
    private lateinit var orderRepository: FakeOrderRepository
    private lateinit var couponRepository: FakeCouponRepository
    private lateinit var outstandingProductRepository: FakeOutstandingProductRepository
    private lateinit var notificationStorage: FakeNotificationSettingStorage
    private lateinit var alarmScheduler: FakeAlarmScheduler
    private lateinit var viewModel: PaymentViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = FakeCartRepository()
        orderRepository = FakeOrderRepository()
        couponRepository = FakeCouponRepository()
        outstandingProductRepository = FakeOutstandingProductRepository()
        notificationStorage = FakeNotificationSettingStorage()
        alarmScheduler = FakeAlarmScheduler()
    }

    private fun initViewModel() {
        viewModel = PaymentViewModel(
            cartRepository = cartRepository,
            orderRepository = orderRepository,
            couponRepository = couponRepository,
            outstandingProductRepository = outstandingProductRepository,
            settingStorage = notificationStorage,
            alarmScheduler = alarmScheduler
        )
    }

    @Test
    fun `초기화 시 장바구니에서 선택된 상품들만 주문 목록에 포함한다`() = runTest {
        // given
        val product1 = Product("과자", 1L, "uri", "상품1", 1000)
        val product2 = Product("음료", 2L, "uri", "상품2", 2000)
        val pp1 = PurchaseProduct(10L, product1, 2)
        val pp2 = PurchaseProduct(20L, product2, 1)
        
        cartRepository.insert(pp1)
        cartRepository.insert(pp2)
        outstandingProductRepository.insertAll(listOf(10L))

        // when
        initViewModel()

        // then
        assertEquals(1, viewModel.uiState.value.order.purchaseProducts.size)
        assertEquals(10L, viewModel.uiState.value.order.purchaseProducts[0].id)
        assertEquals(2000, viewModel.uiState.value.order.totalProductPrice)
    }

    @Test
    fun `쿠폰을 선택하면 할인 금액이 계산된다`() = runTest {
        // given
        val product = Product("과자", 1L, "uri", "상품1", 10000)
        val pp = PurchaseProduct(10L, product, 1)
        cartRepository.insert(pp)
        outstandingProductRepository.insertAll(listOf(10L))
        
        val coupon = FixedCoupon(1, "FIXED", "1000원 할인", LocalDate.now().plusDays(7), 1000, 5000)
        couponRepository.setCoupons(listOf(coupon))

        initViewModel()

        // when
        viewModel.selectCoupon(coupon)

        // then
        assertEquals(coupon, viewModel.uiState.value.selectedCoupon)
        assertEquals(1000, viewModel.uiState.value.discount.totalAmount)
    }

    @Test
    fun `주문 성공 시 쇼핑 화면으로 이동한다`() = runTest {
        // given
        initViewModel()
        testScheduler.advanceUntilIdle()
        val initialCancelCount = alarmScheduler.cancelCalledCount

        val events = mutableListOf<PaymentEvent>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.event.collect { events.add(it) }
        }

        // when
        viewModel.processOrder()

        // then
        assertEquals(initialCancelCount + 1, alarmScheduler.cancelCalledCount)
        assertTrue(events.contains(PaymentEvent.NavigateToShopping))
        
        collectJob.cancel()
    }
}
