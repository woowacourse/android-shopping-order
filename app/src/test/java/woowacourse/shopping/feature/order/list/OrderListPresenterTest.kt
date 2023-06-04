package woowacourse.shopping.feature.order.list

import com.example.domain.model.OrderPreview
import com.example.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation

class OrderListPresenterTest {
    private lateinit var presenter: OrderListContract.Presenter
    private lateinit var view: OrderListContract.View
    private lateinit var orderRepository: OrderRepository
    private val fakeOrderPreview = List(10) { OrderPreview(0, "", "", 0, "", 0) }

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        presenter = OrderListPresenter(view, orderRepository)

        every { orderRepository.getAll(onSuccess = any(), onFailure = any()) } answers {
            firstArg<(List<OrderPreview>) -> Unit>().invoke(fakeOrderPreview)
        }
    }

    @Test
    fun `화면에 주문 목록이 나타난다`() {
        // Given

        // When: 주문 목록을 받아온다.
        presenter.requestOrders()

        // Then: 주문 목록을 띄운다.
        verify { view.showOrders(fakeOrderPreview.map { it.toPresentation() }) }
    }

    @Test
    fun `주문 상세를 확인할 수 있다`() {
        // Given: 주문 목록이 불러와진 상태이다.
        presenter.requestOrders()

        // When: 주문 상세를 요청한다.
        presenter.requestOrderDetail(0)

        // Then: 주문 상세 화면을 띄운다
        verify { view.showOrderDetail(0) }
    }
}
