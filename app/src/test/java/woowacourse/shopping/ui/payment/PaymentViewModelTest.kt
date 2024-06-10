package woowacourse.shopping.ui.payment

import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.cart.CartWithProduct
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponCode
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModel
import java.time.LocalDate

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel

    private lateinit var cartRepository: CartRepository
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setUp() {
        couponRepository = mockk<CouponRepository>()
        cartRepository = mockk<CartRepository>()
        coEvery { cartRepository.getCartItemByCartId(0L).getOrThrow() } returns
            CartWithProduct(
                id = 0L, product = Product(2, "", "", 0, ""),
                quantity = Quantity(value = 5936),
            )
        coEvery { couponRepository.getCoupons().getOrThrow() } returns listOf(fixed5000)
        viewModel =
            PaymentViewModel(
                listOf(0L),
                cartRepository,
                couponRepository,
            )
    }

    @Test
    fun `쿠폰을 클릭한다`() {
        // given

        // when
        viewModel.checkCoupon(0L)
        val actual = viewModel.coupons.getOrAwaitValue()[0]

        // then
        assertThat(actual.isChecked).isTrue()
    }

    @Test
    fun `결제하기 버튼을 누른다`() {
        // given
        coEvery { cartRepository.order(listOf(0L)) } just runs

        // when
        viewModel.paying()
        val actual = viewModel.paying.getValue()

        // then
        assertThat(actual).isEqualTo(Unit)
    }

    companion object {
        private val coupon =
            Coupon(
                id = 0L,
                code = CouponCode.FREESHIPPING,
                description = "",
                expirationDate = LocalDate.of(2024, 6, 8),
                discountType = "",
            )

        private val fixed5000 = Fixed5000(coupon = coupon)
    }
}
