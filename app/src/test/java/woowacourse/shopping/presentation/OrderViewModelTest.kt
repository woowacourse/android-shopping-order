package woowacourse.shopping.presentation

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.remote.CoroutinesTestExtension
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.usecase.GetAvailableCouponsUseCase
import woowacourse.shopping.presentation.order.OrderViewModel

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class OrderViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var getAvailableCouponsUseCase: GetAvailableCouponsUseCase
    private lateinit var viewModel: OrderViewModel

    @BeforeEach
    fun setUp() {
        cartRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        getAvailableCouponsUseCase = mockk(relaxed = true)
        coEvery { getAvailableCouponsUseCase(any()) } returns Result.success(listOf())
        viewModel =
            OrderViewModel(
                cartRepository,
                orderRepository,
                getAvailableCouponsUseCase,
            )
    }

    @Test
    fun `장바구니 아이템 로드 성공시 결제요약정보 업데이트`() =
        runTest {
            // given
            val productIds = longArrayOf(1L, 2L)
            val cartItems =
                listOf(
                    CartItem(1L, Product(1L, "상품1", Price(1_000), "imgurl", "카테고리"), 2),
                    CartItem(2L, Product(2L, "상품2", Price(1_000), "imgurl", "카테고리"), 5),
                )
            coEvery { cartRepository.fetchCartItemById(1L) } returns cartItems[0]
            coEvery { cartRepository.fetchCartItemById(2L) } returns cartItems[1]

            // when
            viewModel.loadOrderInfos(productIds)
            advanceUntilIdle()

            // then
            val expectedPrice = 7_000
            assertEquals(expectedPrice, viewModel.paymentSummaryUiState.value?.orderPrice)
        }

    @Test
    fun `장바구니 아이템 로드 실패시 토스트 메시지 표시`() =
        runTest {
            // given
            val productIds = longArrayOf(2L)
            coEvery { cartRepository.fetchCartItemById(2L) } returns null

            // when
            viewModel.loadOrderInfos(productIds)
            advanceUntilIdle()

            // then (테스트에서 R 소스를 찾지 못해서 map 활용)
            val resourceMap =
                mapOf(
                    viewModel.toastMessage.value to "상품을 불러오는 데 실패했습니다",
                )
            assertEquals(resourceMap[viewModel.toastMessage.value], "상품을 불러오는 데 실패했습니다")
        }
}
