package woowacourse.shopping.presentation

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.usecase.GetAvailableCouponUseCase
import woowacourse.shopping.fixture.CouponFixture
import woowacourse.shopping.fixture.ProductsFixture
import woowacourse.shopping.presentation.Extra.KEY_SELECT_ITEMS
import woowacourse.shopping.presentation.order.OrderViewModel

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var getAvailableCouponUseCase: GetAvailableCouponUseCase
    private lateinit var viewModel: OrderViewModel

    @BeforeEach
    fun setUp() {
        val savedStateHandle =
            SavedStateHandle(mapOf(KEY_SELECT_ITEMS to ProductsFixture.dummyCartUiModels))
        orderRepository = mockk()
        getAvailableCouponUseCase = mockk()

        coEvery { orderRepository.order(any()) } returns Result.success(Unit)

        coEvery {
            getAvailableCouponUseCase(
                any(),
                any(),
            )
        } returns Result.success(CouponFixture.couponList)

        viewModel = OrderViewModel(savedStateHandle, getAvailableCouponUseCase, orderRepository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `이전_화면에서_넘어온_선택된_아이템_정보로_초기_주문정보를_세팅한다`() {
        val initialOrderSummary = viewModel.orderSummary.getOrAwaitValue()
        assertThat(initialOrderSummary.productTotalPrice).isEqualTo(15_000)
        assertThat(initialOrderSummary.deliveryFee).isEqualTo(3_000)
        assertThat(initialOrderSummary.finalPrice).isEqualTo(18_000)
    }

    @Test
    fun `쿠폰_목록을_불러온다`() =
        runTest {
            assertThat(viewModel.coupons.getOrAwaitValue().size).isEqualTo(4)
        }

    @Test
    fun `주문하기에_성공하면_orderSuccessEvent_true를_반환한다`() =
        runTest {
            viewModel.order()

            assertThat(viewModel.orderSuccessEvent.getOrAwaitValue()).isEqualTo(true)
        }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
