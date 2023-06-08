package woowacourse.shopping.feature.order

import com.example.domain.datasource.productsDatasource
import com.example.domain.model.cart.CartProduct
import com.example.domain.model.point.PointInfo
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class OrderPresenterTest {
    private lateinit var view: OrderContract.View
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var cartRepository: CartRepository
    private lateinit var pointRepository: PointRepository
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        pointRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderPresenter(view, cartRepository, pointRepository, orderRepository)
        every { cartRepository.getAll() } returns List(2) { cartProduct }
    }

    @Test
    fun `장바구니에서 선택한 상품들을 가져온다`() {
        // given
        val slot = slot<List<CartProductUiModel>>()
        justRun { view.initOrderProducts(capture(slot)) }

        // when
        presenter.loadProducts()

        // then
        val actual = List(2) { cartProduct.toPresentation() }
        val expected = slot.captured

        assertEquals(expected, actual)
        verify { view.initOrderProducts(any()) }
    }

    @Test
    fun `사용자가 보유한 point를 가져온다`() {
        // given
        val slot = slot<Int>()
        justRun { view.setUpView(capture(slot), any()) }
        every {
            pointRepository.getPoint(any(), any())
        } answers {
            firstArg<(PointInfo) -> Unit>().invoke(PointInfo(100, 10))
        }

        // when
        presenter.loadPayment()

        // then
        val expected = slot.captured
        assertEquals(expected, 100)
        verify { view.setUpView(any(), any()) }
    }

    @Test
    fun `주문하면 저장소에 정보를 저장한다`() {
        // given
        every {
            orderRepository.addOrder(any(), any(), callback = any())
        } answers {
            thirdArg<(Result<Unit>) -> Unit>().invoke(Result.success(Unit))
        }

        // when
        presenter.orderProducts(100)

        // then
        verify { view.successOrder() }
    }

    private val cartProduct = CartProduct(0L, productsDatasource[0], 1, true)
}
