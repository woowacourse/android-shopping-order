package woowacourse.shopping.presentation.ui.payment

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProducts
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fixedCoupon
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.presentation.CoroutinesTestExtension
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class PaymentViewModelTest {
    @RelaxedMockK
    private lateinit var orderRepository: OrderRepository

    @RelaxedMockK
    private lateinit var couponRepository: CouponRepository

    @RelaxedMockK
    private lateinit var recentProductRepository: RecentProductRepository

    @InjectMockKs
    private lateinit var viewModel: PaymentViewModel

    @Test
    fun `사용할 수 있는 쿠폰을 로드한다`() =
        runTest {
            coEvery { couponRepository.getAll() } returns Result.success(listOf(fixedCoupon))

            viewModel.setPaymentUiModel(PaymentUiState(cartProducts = cartProducts))
            viewModel.loadCoupons()

            val state = viewModel.uiState.getOrAwaitValue()

            state.couponUiModels.size shouldBe 1
        }
}
