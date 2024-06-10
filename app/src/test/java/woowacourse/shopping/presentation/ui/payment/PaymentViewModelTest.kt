package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.model.coupon.FixedAmountDiscountPolicy
import woowacourse.shopping.domain.model.coupon.MinimumPurchaseAmountCondition
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS_WRAPPER
import woowacourse.shopping.remote.api.DummyData.COUPONS_STATE

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class, MockKExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel

    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var couponRepository: CouponRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun setUp() {
        coEvery {
            couponRepository.getCoupons()
        } returns Result.success(COUPONS_STATE)

        val initialState =
            mapOf(PaymentActivity.PUT_EXTRA_CARTS_WRAPPER_KEY to CARTS_WRAPPER)
        savedStateHandle = SavedStateHandle(initialState)

        viewModel =
            PaymentViewModel(
                savedStateHandle,
                couponRepository,
                orderRepository,
            )
    }

    @Test
    fun `수량이 2개 이상인 상품이 있고, 총 금액이 100_000원 이상일 때 5,000원 할인 쿠폰과 5만원 이상 구매 시 무료 배송 쿠폰을 받을 수 있다`() =
        runTest {
            delay(2000)
            val actual = viewModel.uiState.getOrAwaitValue()
            assertThat(actual.coupons).anyMatch { it.discountPolicy is FixedAmountDiscountPolicy }
            assertThat(actual.coupons).anyMatch { it.couponCondition is MinimumPurchaseAmountCondition }
        }

    @Test
    fun `주문 상품을 주문할 수 있다`() =
        runTest {
            // given
            coEvery { orderRepository.insertOrder(CARTS_WRAPPER.cartUiModels.map { it.id }) } returns
                Result.success(Unit)

            // when
            viewModel.payment()

            // then
            val actual = viewModel.navigateAction.getOrAwaitValue()
            assertThat(actual.value).isEqualTo(PaymentNavigateAction.NavigateToProductList)
        }
}
