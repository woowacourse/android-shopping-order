package woowacourse.shopping.view

import androidx.lifecycle.MutableLiveData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.CouponValidate
import woowacourse.shopping.domain.coupon.CouponValidator
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ext.getOrAwaitValue
import woowacourse.shopping.fixture.shoppingCartFixture1
import woowacourse.shopping.view.order.OrderUiEvent
import woowacourse.shopping.view.order.state.CouponState
import woowacourse.shopping.view.order.state.OrderUiState
import woowacourse.shopping.view.order.vm.OrderViewModel
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private val couponRepository: CouponRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val couponValidate: CouponValidate = CouponValidator()
    private val couponFactory: CouponApplierFactory = CouponApplierFactory()

    private lateinit var viewModel: OrderViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            OrderViewModel(
                couponRepository,
                orderRepository,
                couponValidate,
                couponFactory,
            )
    }

    @Test
    fun `쿠폰 목록 호출 후 필터링된 쿠폰으로 상태가 초기화된다`() =
        runTest {
            // given
            val carts = listOf(shoppingCartFixture1)
            val fixedCoupon =
                FixedCoupon(
                    id = 1,
                    code = "FIXED5000",
                    discount = 5000,
                    description = "",
                    expirationDate = LocalDate.parse("2025-11-30"),
                    discountType = "",
                    minimumAmount = 0,
                )
            val coupons = listOf(fixedCoupon)

            coEvery { couponRepository.getCoupons() } returns Result.success(coupons)

            // when
            viewModel.loadCoupons(carts)

            val expected = listOf(CouponState(item = fixedCoupon, checked = false))

            // then
            val state = viewModel.uiState.getOrAwaitValue()
            assertEquals(carts, state.order)
            assertEquals(expected, state.coupons)
        }

    @Test
    fun `주문 성공 시 OrderComplete가 이벤트 발생한다`() =
        runTest {
            // given
            val carts = listOf(shoppingCartFixture1)
            val coupons = emptyList<Coupon>()
            val orderCartIds = listOf(1L)

            viewModel.setFakeUiState(OrderUiState.of(carts, coupons, 3000))
            coEvery { orderRepository.createOrder(orderCartIds) } returns Result.success(Unit)

            // when
            viewModel.sendOrder()

            // then
            val event = viewModel.uiEvent.getValue()
            assertEquals(OrderUiEvent.OrderComplete, event)
        }
}

fun OrderViewModel.setFakeUiState(state: OrderUiState) {
    val field = OrderViewModel::class.java.getDeclaredField("_uiState")
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    (field.get(this) as MutableLiveData<OrderUiState>).value = state
}
