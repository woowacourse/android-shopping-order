package woowacourse.shopping.feature.order.confirm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.BaseResponse
import com.example.domain.model.CartProduct
import com.example.domain.model.MoneySalePolicy
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.feature.CartFixture
import woowacourse.shopping.feature.Product
import woowacourse.shopping.feature.getOrAwaitValue
import woowacourse.shopping.mapper.toPresentation

internal class OrderConfirmPresenterTest {
    private lateinit var view: OrderConfirmContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: OrderConfirmContract.Presenter

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setInit() {
        view = mockk()
        cartRepository = mockk()
        orderRepository = mockk()
    }

    @Test
    fun `선택한 장바구니 아이템들의 정보를 불러오면_주문 목록 내용을 보여주고, 할인 적용 내용 보여주고, 최종 결제 금액을 보여준다`() {
        // given
        val cartIds = listOf(1L, 2L, 4L)
        presenter =
            OrderConfirmPresenter(view, MoneySalePolicy(), cartRepository, orderRepository, cartIds)

        val mockCartProducts = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 2000), 3),
            Triple(2L, Product(2L, 13000), 2),
            Triple(3L, Product(3L, 9000), 1),
            Triple(4L, Product(4L, 4000), 2),
        )

        every { cartRepository.fetchAll(any()) } answers {
            val successBlock = arg<(BaseResponse<List<CartProduct>>) -> Unit>(0)
            successBlock(BaseResponse.SUCCESS(mockCartProducts))
        }
        every { view.showSaleInfo() } just Runs
        every { view.setSaleInfo(any()) } just Runs
        every { view.setPayInfo(any(), any()) } just Runs
        every { view.setFinalPayInfo(any()) } just Runs

        // when
        presenter.loadSelectedCarts()

        // then
        val actual = presenter.cartProducts.getOrAwaitValue()
        val expected = CartFixture.getMockCarts(
            Triple(1L, Product(1L, 2000), 3),
            Triple(2L, Product(2L, 13000), 2),
            Triple(4L, Product(4L, 4000), 2),
        ).map { it.toPresentation() }
        assert(actual == expected)

        // and
        verify { view.setSaleInfo(any()) }
        verify { view.showSaleInfo() }
        // and
        verify { view.setPayInfo(any(), any()) }
        // and
        verify { view.setFinalPayInfo(any()) }
    }

    @Test
    fun `주문하기 버튼을 클릭하면 주문이 완료되고 화면이 종료된다`() {
        // given
        val cartIds = listOf(1L, 2L, 4L)
        presenter =
            OrderConfirmPresenter(view, MoneySalePolicy(), cartRepository, orderRepository, cartIds)

        val cartIdsSlot = slot<List<Long>>()
        val paymentSlot = slot<Int>()
        every {
            orderRepository.addOrder(
                capture(cartIdsSlot),
                capture(paymentSlot),
                any()
            )
        } answers {
            val successBlock = arg<(BaseResponse<Long>) -> Unit>(2)
            successBlock(BaseResponse.SUCCESS(1L))
        }

        every { view.showOrderSuccess(any()) } just Runs
        every { view.exitScreen() } just Runs

        // when
        presenter.requestOrder()

        // then
        verify { view.showOrderSuccess(cartIds) }
        verify { view.exitScreen() }
    }
}
