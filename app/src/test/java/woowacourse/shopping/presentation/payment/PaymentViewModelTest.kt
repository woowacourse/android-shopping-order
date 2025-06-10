package woowacourse.shopping.presentation.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponType
import woowacourse.shopping.domain.usecase.ApplyCouponPolicyUseCase
import woowacourse.shopping.fixture.CouponFixture
import woowacourse.shopping.fixture.FakeCouponRepository
import woowacourse.shopping.fixture.FakeOrderRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.TestTimeProvider
import woowacourse.shopping.util.getOrAwaitValue
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PaymentViewModel
    private lateinit var fakeCouponRepository: FakeCouponRepository
    private lateinit var fakeOrderRepository: FakeOrderRepository
    private lateinit var initialCheckedItems: List<ProductUiModel>

    private lateinit var testTimeProvider: TestTimeProvider

    @BeforeEach
    fun setUp() {
        initialCheckedItems =
            listOf(
                ProductUiModel(
                    id = 1L,
                    name = "아이스 카페 아메리카노",
                    imageUrl = "",
                    price = 10000,
                    quantity = 2,
                ),
            )

        testTimeProvider = TestTimeProvider(LocalDateTime.of(2025, 6, 10, 5, 0)) // 새벽 5시

        val coupons =
            listOf(
                Coupon(
                    id = 1L,
                    description = "5,000원 할인 쿠폰",
                    expirationDate = "2025-10-11",
                    code = CouponType.FIXED5000.name,
                    discount = 5000,
                    minimumAmount = 10000,
                    discountType = "fixed",
                    buyQuantity = null,
                    getQuantity = null,
                    availableTime = null,
                ),
                Coupon(
                    id = 2L,
                    description = "무료배송 쿠폰",
                    expirationDate = "2025-08-31",
                    code = CouponType.FREESHIPPING.name,
                    discount = null,
                    minimumAmount = 50000,
                    discountType = "freeShipping",
                    buyQuantity = null,
                    getQuantity = null,
                    availableTime = null,
                ),
                Coupon(
                    id = 3L,
                    description = "미라클모닝 30%",
                    expirationDate = "2025-07-31",
                    code = CouponType.MIRACLESALE.name,
                    discount = 30,
                    minimumAmount = null,
                    discountType = "percentage",
                    buyQuantity = null,
                    getQuantity = null,
                    availableTime = Coupon.AvailableTime(start = "04:00:00", end = "07:00:00"),
                ),
                Coupon(
                    id = 4L,
                    description = "BOGO 쿠폰",
                    expirationDate = "2025-06-30",
                    code = CouponType.BOGO.name,
                    discount = null,
                    minimumAmount = null,
                    discountType = "buyXgetY",
                    buyQuantity = 2,
                    getQuantity = 1,
                    availableTime = null,
                ),
            )

        fakeCouponRepository =
            FakeCouponRepository(
                coupons = coupons,
                timeProvider = testTimeProvider,
            )

        fakeOrderRepository = FakeOrderRepository(0)

        viewModel =
            PaymentViewModel(
                couponRepository = fakeCouponRepository,
                orderRepository = fakeOrderRepository,
                initialCheckedItems = initialCheckedItems,
                applyCouponPolicyUseCase = ApplyCouponPolicyUseCase(),
            )
    }

    @Test
    fun `getCoupons 호출 시 유효한 쿠폰들만 필터링 되어 업데이트 된다`() =
        runTest {
            viewModel.getCoupons()

            val coupons = viewModel.coupons.getOrAwaitValue()

            assertThat(coupons.map { it.code }).containsExactlyInAnyOrder(
                CouponType.FIXED5000.name,
                CouponType.MIRACLESALE.name,
                CouponType.BOGO.name,
            )
        }

    @Test
    fun `onCheckCoupon 호출 시 할인금액과 배송비가 정확히 계산된다`() {
        val coupon =
            Coupon(
                id = 1L,
                description = "5,000원 할인 쿠폰",
                expirationDate = "2025-10-11",
                code = CouponType.FIXED5000.name,
                discount = 5000,
                minimumAmount = 10000,
                discountType = "fixed",
                buyQuantity = null,
                getQuantity = null,
                availableTime = null,
            )

        viewModel.onCheckCoupon(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val deliveryCharge = viewModel.deliverCharge.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        assertThat(discount).isEqualTo(5000L)
        assertThat(deliveryCharge).isEqualTo(3000L)
        val expectedTotal = viewModel.initialOrderPrice - discount + deliveryCharge
        assertThat(totalAmount).isEqualTo(expectedTotal)
    }

    @Test
    fun `만료된 쿠폰은 getCoupons 결과에 포함되지 않는다`() =
        runTest {
            val expiredCoupon =
                Coupon(
                    id = 99L,
                    description = "ExpiredCoupon",
                    expirationDate = "2020-01-01",
                    code = CouponType.FIXED5000.name,
                    discount = 5000,
                    minimumAmount = 5000,
                    discountType = "fixed",
                    buyQuantity = 0L,
                    getQuantity = 0L,
                    availableTime = null,
                )

            val couponRepository =
                FakeCouponRepository(
                    coupons = listOf(expiredCoupon),
                    timeProvider = testTimeProvider,
                )

            viewModel =
                PaymentViewModel(
                    couponRepository = couponRepository,
                    orderRepository = fakeOrderRepository,
                    initialCheckedItems = initialCheckedItems,
                    applyCouponPolicyUseCase = ApplyCouponPolicyUseCase(),
                )

            viewModel.getCoupons()

            val coupons = viewModel.coupons.getOrAwaitValue()

            assertThat(coupons).isEmpty()
        }

    @Test
    fun `resetOrderInfo 호출 시 할인금액과 배송비는 초기값으로`() {
        viewModel.resetOrderInfo()

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val deliveryCharge = viewModel.deliverCharge.getOrAwaitValue()

        assertThat(discount).isEqualTo(0L)
        assertThat(deliveryCharge).isEqualTo(3000L)
    }

    @Test
    fun `updateOrderInfo 호출 시 totalAmount도 갱신된다`() {
        val coupon =
            Coupon(
                id = 1L,
                description = "5,000원 할인 쿠폰",
                expirationDate = "2025-10-11",
                code = CouponType.FIXED5000.name,
                discount = 5000,
                minimumAmount = 10000,
                discountType = "fixed",
                buyQuantity = null,
                getQuantity = null,
                availableTime = null,
            )

        viewModel.updateOrderInfo(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        val expected =
            (viewModel.initialOrderPrice - discount + viewModel.deliverCharge.getOrAwaitValue()).coerceAtLeast(
                0,
            )
        assertThat(totalAmount).isEqualTo(expected)
    }

    @Test
    fun `FIXED5000 쿠폰 적용 시 totalAmount는 고정 할인 적용`() {
        val coupon = CouponFixture.fixed5000Coupon()

        viewModel.updateOrderInfo(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        val expected =
            (viewModel.initialOrderPrice - discount + viewModel.deliverCharge.getOrAwaitValue()).coerceAtLeast(
                0,
            )
        assertThat(discount).isEqualTo(5000L)
        assertThat(totalAmount).isEqualTo(expected)
    }

    @Test
    fun `FREESHIPPING 쿠폰 적용 시 배송비는 0원이 되고 totalAmount 계산`() {
        val coupon = CouponFixture.freeShippingCoupon()

        viewModel.updateOrderInfo(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val deliveryCharge = viewModel.deliverCharge.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        assertThat(deliveryCharge).isEqualTo(0L)
        val expected = (viewModel.initialOrderPrice - discount + deliveryCharge).coerceAtLeast(0)
        assertThat(totalAmount).isEqualTo(expected)
    }

    @Test
    fun `MIRACLESALE 쿠폰 적용 시 퍼센트 할인 적용`() {
        val coupon = CouponFixture.miracleSaleCoupon()

        viewModel.updateOrderInfo(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val deliveryCharge = viewModel.deliverCharge.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        val expectedDiscount = (viewModel.initialOrderPrice * 30 / 100)
        assertThat(discount).isEqualTo(expectedDiscount)

        val expected = (viewModel.initialOrderPrice - discount + deliveryCharge).coerceAtLeast(0)
        assertThat(totalAmount).isEqualTo(expected)
    }

    @Test
    fun `BOGO 쿠폰 적용 시 할인액과 배송비 검증`() {
        val coupon = CouponFixture.bogoCoupon()

        viewModel.updateOrderInfo(coupon)

        val discount = viewModel.discountAmount.getOrAwaitValue()
        val deliveryCharge = viewModel.deliverCharge.getOrAwaitValue()
        val totalAmount = viewModel.totalAmount.getOrAwaitValue()

        val expectedDiscount =
            (coupon.getQuantity ?: 0L) * initialCheckedItems.maxByOrNull { it.price }!!.price

        assertThat(discount).isEqualTo(expectedDiscount)

        val expected = (viewModel.initialOrderPrice - discount + deliveryCharge).coerceAtLeast(0)
        assertThat(totalAmount).isEqualTo(expected)
    }

    @Test
    fun `초기 상태에서 totalAmount는 주문금액 + 배송비`() {
        val total = viewModel.totalAmount.getOrAwaitValue()
        val expected = viewModel.initialOrderPrice + 3000L

        assertThat(total).isEqualTo(expected)
    }
}
