package woowacourse.shopping.feature.userInfo

import com.example.domain.datasource.orderDataSource
import com.example.domain.model.Order
import com.example.domain.model.Point
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.OrderUiModel

class UserInfoPresenterTest {
    private lateinit var presenter: UserInfoContract.Presenter
    private lateinit var view: UserInfoContract.View
    private lateinit var orderRepository: OrderRepository
    private lateinit var pointRepository: PointRepository

    @Before
    fun setup() {
        view = mockk()
        orderRepository = mockk()
        pointRepository = mockk(relaxed = true)

        presenter = UserInfoPresenter(view, orderRepository, pointRepository)
    }

    @Test
    fun `주문 내역을 노출한다`() {
        every {
            orderRepository.getOrders(page = any(), onSuccess = any(), onFailure = any())
        } answers {
            secondArg<(List<Order>) -> Unit>().invoke(
                orderDataSource.subList(0, 5)
            )
        }
        val slot = slot<List<OrderUiModel>>()
        every { view.showOrders(capture(slot)) } just Runs

        presenter.loadOrders()

        val actual = slot.captured
        val expected = orderDataSource.subList(0, 5).map { it.toPresentation() }
        assert(actual == expected)
    }

    @Test
    fun `보유 포인트와 소멸 예정 포인트를 노출한다`() {
        every {
            pointRepository.getPoint(onSuccess = any(), onFailure = any())
        } answers { firstArg<(Point) -> Unit>().invoke(Point(2000, 1000)) }
        every { view.showPoint(any()) } just Runs

        presenter.loadPoint()

        verify { view.showPoint(any()) }
    }
}
