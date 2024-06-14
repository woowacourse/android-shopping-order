package woowacourse.shopping.presentation.ui.shoppingcart.payment

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel

    @MockK
    private lateinit var couponRepository: CouponRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    private lateinit var savedStateHandle: SavedStateHandle

    val coupons = DummyData.COPOUNS

    @BeforeEach
    fun setUp() {
        coEvery {
            couponRepository.getCoupons()
        } returns Result.success(coupons)

        val initialState = mapOf(PaymentFragment.PUT_EXTRA_CART_IDS_KEY to DummyData.CART_ARRAY)
        savedStateHandle = SavedStateHandle(initialState)

        viewModel =
            PaymentViewModel(
                savedStateHandle,
                couponRepository,
                orderRepository,
            )
    }

    @Test
    fun `주문 상품을 주문할 수 있다`() {
        coEvery { orderRepository.insertOrderByIds(DummyData.CART_ARRAY.map { it.id }) } returns Result.success(Unit)

        viewModel.makeAPayment()

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        assertThat(actual.value).isEqualTo(PaymentNavigateAction.NavigateToProductList)
    }
}
