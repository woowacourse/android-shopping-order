package woowacourse.shopping.ui.payment

import CoroutinesTestExtension
import InstantTaskExecutorExtension
import TestFixture.buyXGetYCoupon
import TestFixture.fixedDiscountCoupon
import TestFixture.freeShippingCoupon
import TestFixture.orderInformation
import getOrAwaitValue
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.repository.coupon.CouponRepository
import woowacourse.shopping.domain.repository.order.OrderRepository

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel
    private lateinit var orderRepository: OrderRepository
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setup() {
        orderRepository = mockk()
        couponRepository = mockk()
        viewModel =
            PaymentViewModel(
                orderInformation,
                orderRepository,
                couponRepository,
            )
    }

    @Test
    fun 주문_완료_전_결제_성공_여부는_false이다() {
        // then
        val actual = viewModel.isPaymentSuccess.getValue()
        assertThat(actual).isFalse()
    }

    @Test
    fun `주문이_완료되면_결제_성공_여부는_true이다`() =
        runTest {
            // given
            coEvery { orderRepository.orderCartItems(orderInformation.getCartItemIds()) } returns ResponseResult.Success(Unit)

            // when
            viewModel.createOrder()

            // then
            val actual = viewModel.isPaymentSuccess.getValue()
            assertThat(actual).isTrue()
        }

    @Test
    fun `가격이 20만원인 상품 3개와 가격이 5만원인 상품 2개를 주문한다면 주문 금액은 70만원이다`() {
        // when
        viewModel.loadInitialPaymentInformation()

        // then
        val actual = viewModel.orderAmount.getOrAwaitValue()
        assertThat(actual).isEqualTo(700_000)
    }

    @Test
    fun `쿠폰을 적용하기 전 쿠폰 할인 금액은 0원이다`() {
        // then
        val actual = viewModel.discountAmount.value
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `쿠폰을 적용하기 전 기본 배송비는 3천원이다`() {
        // when
        viewModel.loadInitialPaymentInformation()

        // then
        val actual = viewModel.shippingFee.getOrAwaitValue()
        assertThat(actual).isEqualTo(3_000)
    }

    @Test
    fun `가격이 20만원인 상품 3개와 가격이 5만원인 상품 2개를 주문한다면 쿠폰을 적용하기 전 총 결제 금액은 70만 3천원이다`() {
        // when
        viewModel.loadInitialPaymentInformation()

        // then
        val actual = viewModel.totalPaymentAmount.getOrAwaitValue()
        assertThat(actual).isEqualTo(703_000)
    }

    @Test
    fun `장바구니 상품 중 갯수가 2인 항목이 있다면 2개 구매 시 1개 무료 쿠폰을 불러온다`() =
        runTest {
            // given
            coEvery { couponRepository.loadCoupons(orderInformation) } returns
                ResponseResult.Success(
                    listOf(buyXGetYCoupon),
                )

            // when
            viewModel.loadCoupons()

            // then
            val actual = viewModel.couponsUiModel.getOrAwaitValue().map { it.discountType }
            assertThat(actual).contains(BuyXGetYCoupon.TYPE)
        }

    @Test
    fun `주문 금액이 10만원 이상이라면 5,000원 할인, 배송비 할인 쿠폰을 불러온다`() =
        runTest {
            // given
            coEvery { couponRepository.loadCoupons(orderInformation) } returns
                ResponseResult.Success(
                    listOf(fixedDiscountCoupon, freeShippingCoupon),
                )

            // when
            viewModel.loadCoupons()

            // then
            val actual = viewModel.couponsUiModel.getOrAwaitValue().map { it.discountType }
            assertAll(
                { assertThat(actual).contains(FixedDiscountCoupon.TYPE) },
                { assertThat(actual).contains(FreeShippingCoupon.TYPE) },
            )
        }

    @Test
    fun `5,000원 할인 쿠폰이 선택되었다면 할인 금액은 -5천원이고, 총 결제 금액은 69만 8천원이다 `() {
        // given
        val selectedCouponId: Long = 1
        val isChecked = true
        coEvery { couponRepository.loadCoupons(orderInformation) } returns
            ResponseResult.Success(
                listOf(fixedDiscountCoupon, freeShippingCoupon, buyXGetYCoupon),
            )
        every { couponRepository.calculateDiscountAmount(orderInformation, selectedCouponId, isChecked) } returns -5_000
        every { couponRepository.calculateShippingFee(orderInformation, selectedCouponId, isChecked) } returns 3_000
        every { couponRepository.calculateTotalAmount(orderInformation, selectedCouponId, isChecked) } returns 698_000

        // when
        viewModel.loadCoupons()
        viewModel.onCouponSelected(selectedCouponId)

        // then
        val actualDiscountAmount = viewModel.discountAmount.getOrAwaitValue()
        val actualTotalAmount = viewModel.totalPaymentAmount.getOrAwaitValue()
        assertAll(
            { assertThat(actualDiscountAmount).isEqualTo(-5_000) },
            { assertThat(actualTotalAmount).isEqualTo(698_000) },
        )
    }

    @Test
    fun `가격이 5만원인 상품의 수가 2개이고, 2개 구매 시 1개 무료 쿠폰이 선택되었다면 할인 금액은 -5만원이고, 총 결제 금액은 65만 3천원이다 `() {
        // given
        val selectedCouponId: Long = 2
        val isChecked = true
        coEvery { couponRepository.loadCoupons(orderInformation) } returns
            ResponseResult.Success(
                listOf(fixedDiscountCoupon, freeShippingCoupon, buyXGetYCoupon),
            )
        every { couponRepository.calculateDiscountAmount(orderInformation, selectedCouponId, isChecked) } returns -50_000
        every { couponRepository.calculateShippingFee(orderInformation, selectedCouponId, isChecked) } returns 3_000
        every { couponRepository.calculateTotalAmount(orderInformation, selectedCouponId, isChecked) } returns 653_000

        // when
        viewModel.loadCoupons()
        viewModel.onCouponSelected(selectedCouponId)

        // then
        val actualDiscountAmount = viewModel.discountAmount.getOrAwaitValue()
        val actualTotalAmount = viewModel.totalPaymentAmount.getOrAwaitValue()
        assertAll(
            { assertThat(actualDiscountAmount).isEqualTo(-50_000) },
            { assertThat(actualTotalAmount).isEqualTo(653_000) },
        )
    }

    @Test
    fun `5만원 이상 구매 시 무료 배송 쿠폰이 선택되었다면 할인 금액은 -3천원, 배송비는 0원, 총 결제 금액은 70만원이다 `() {
        // given
        val selectedCouponId: Long = 3
        val isChecked = true
        coEvery { couponRepository.loadCoupons(orderInformation) } returns
            ResponseResult.Success(
                listOf(fixedDiscountCoupon, freeShippingCoupon, buyXGetYCoupon),
            )
        every { couponRepository.calculateDiscountAmount(orderInformation, selectedCouponId, isChecked) } returns -3_000
        every { couponRepository.calculateShippingFee(orderInformation, selectedCouponId, isChecked) } returns 0
        every { couponRepository.calculateTotalAmount(orderInformation, selectedCouponId, isChecked) } returns 700_000

        // when
        viewModel.loadCoupons()
        viewModel.onCouponSelected(selectedCouponId)

        // then
        val actualDiscountAmount = viewModel.discountAmount.getOrAwaitValue()
        val actualShippingFee = viewModel.shippingFee.getOrAwaitValue()
        val actualTotalAmount = viewModel.totalPaymentAmount.getOrAwaitValue()
        assertAll(
            { assertThat(actualDiscountAmount).isEqualTo(-3_000) },
            { assertThat(actualShippingFee).isEqualTo(0) },
            { assertThat(actualTotalAmount).isEqualTo(700_000) },
        )
    }
}
