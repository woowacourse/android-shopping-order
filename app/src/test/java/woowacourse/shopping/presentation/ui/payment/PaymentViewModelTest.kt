package woowacourse.shopping.presentation.ui.payment

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.cartProducts
import woowacourse.shopping.coupons
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.Repository

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(MockKExtension::class)
class PaymentViewModelTest {
    private lateinit var repository: Repository
    private lateinit var ids: List<Long>
    private lateinit var viewModel: PaymentViewModel

    @BeforeEach
    fun setup() {
        repository = mockk()
        ids = listOf(1L, 2L, 3L)

        coEvery { repository.getCartItems(any(), any()) } returns Result.success(cartProducts)
        viewModel = PaymentViewModel(repository, ids)
    }

    @Test
    fun `viewModel이 초기화되면 쿠폰을 불러온다`() = runTest {
        // given
        coEvery { repository.getCoupons() } returns Result.success(coupons)
        // when
        viewModel.getCoupons()
        // then
        coVerify { repository.getCoupons() }
    }

    @Test
    fun `주문을 하면 성공적으로 주문이 완료된다`() = runTest {
        // given
        coEvery { repository.submitOrders(OrderRequest(ids.map { it.toInt() })) } returns Result.success(
            Unit
        )
        // when
        viewModel.order()
        // then
        coVerify { repository.submitOrders(OrderRequest(ids.map { it.toInt() })) }
    }
}
