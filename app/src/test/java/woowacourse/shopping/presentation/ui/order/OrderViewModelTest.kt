package woowacourse.shopping.presentation.ui.order

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedDiscountStrategy
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.getOrAwaitValue
import java.time.LocalDate

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class OrderViewModelTest {
    @MockK
    private lateinit var couponRepository: CouponRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    private lateinit var viewModel: OrderViewModel

    @Test
    fun `10,000원 주문 시 1,000원 할인되는 쿠폰을 선택할 경우, 배송비 3,000원을 포함한 12,000원을 계산한다`() {
        val totalPriceWithoutDiscount = 10_000L
        val coupons = listOf(Coupon(id = 1, discountAmount = 1_000))
        coEvery { couponRepository.findCoupons(any()) } returns Result.success(coupons)

        viewModel = OrderViewModel(couponRepository, orderRepository, emptyList(), totalPriceWithoutDiscount)
        viewModel.selectCoupon(1)

        val actual = viewModel.totalPriceWithDiscount.getOrAwaitValue()
        val expected = 12_000L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `주문 완료시 선택된 상품들을 주문한다`() {
        val selectedCartIds: List<Long> = listOf(0L, 1L, 2L)
        coEvery { couponRepository.findCoupons(selectedCartIds) } returns Result.success(emptyList())
        coEvery { orderRepository.completeOrder(selectedCartIds) } returns Result.success(Unit)

        viewModel = OrderViewModel(couponRepository, orderRepository, selectedCartIds, 0)
        viewModel.completeOrder()

        val actual = viewModel.completeOrder.getOrAwaitValue().getContentIfNotHandled()
        val expected = true
        assertThat(actual).isEqualTo(expected)
    }
}

private fun Coupon(
    id: Int,
    discountAmount: Long,
): Coupon {
    return Coupon(id, "", "", LocalDate.now(), FixedDiscountStrategy(emptyList(), discountAmount.toInt(), 0))
}
