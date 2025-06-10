package woowacourse.shopping.presentation.view

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.fixture.FakeCouponRepository
import woowacourse.shopping.fixture.FakeOrderRepository
import woowacourse.shopping.fixture.productsFixture
import woowacourse.shopping.presentation.view.payment.PaymentViewModel
import woowacourse.shopping.presentation.view.payment.event.PaymentMessageEvent
import woowacourse.shopping.presentation.view.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.view.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.view.util.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel
    private val orderProducts = productsFixture.take(10)

    @BeforeEach
    fun setup() {
        val fakeCouponRepository = FakeCouponRepository()
        val fakeOrderRepository =
            FakeOrderRepository(
                cartProducts = orderProducts.map { CartProduct(it.id, it.toDomain(), 1) },
            )

        val handle = SavedStateHandle(mapOf("ORDER_PRODUCT_IDS" to orderProducts.map { it.id }))
        viewModel = PaymentViewModel(handle, fakeCouponRepository, fakeOrderRepository)
    }

    @Test
    fun `결제 요약 정보가 로드된다`() {
        // When
        val paymentSummary = viewModel.paymentSummary.getOrAwaitValue()

        // Then
        val orderPrice = orderProducts.sumOf { it.price }
        assertAll(
            { assertThat(paymentSummary).isNotNull },
            { assertThat(paymentSummary.orderPrice).isEqualTo(orderPrice) },
        )
    }

    @Test
    fun `쿠폰이 정상적으로 로드된다`() {
        // When
        val coupons = viewModel.coupons.getOrAwaitValue()

        // Then
        assertThat(coupons).isNotEmpty
    }

    @Test
    fun `쿠폰을 선택하면 선택 상태가 변경되고 결제 요약 정보가 갱신된다`() {
        // Give
        val initialSummary = viewModel.paymentSummary.getOrAwaitValue()
        val coupon = viewModel.coupons.getOrAwaitValue().first()

        // When
        viewModel.onSelectCoupon(coupon.id)
        val updatedSummary = viewModel.paymentSummary.getOrAwaitValue()
        val updatedCoupon = viewModel.coupons.getOrAwaitValue().first { it.id == coupon.id }

        // Then
        assertAll(
            { assertThat(updatedSummary).isNotEqualTo(initialSummary) },
            { assertThat(updatedCoupon.isSelected).isTrue() },
        )
    }

    @Test
    fun `주문에 성공하면 성공 이벤트와 토스트가 발생한다`() {
        // When
        viewModel.postOrder()

        val successEvent = viewModel.orderSuccessEvent.getValue()
        val toastEvent = viewModel.toastEvent.getValue()

        // Then
        assertAll(
            { assertThat(successEvent).isEqualTo(Unit) },
            { assertThat(toastEvent).isEqualTo(PaymentMessageEvent.ORDER_SUCCESS) },
        )
    }
}
