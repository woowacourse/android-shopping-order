package woowacourse.shopping.ui.orderhistory

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.OrderFixture
import woowacourse.shopping.ui.mapper.toUiModel
import java.util.concurrent.CompletableFuture

class OrderHistoryPresenterTest {

    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var view: OrderHistoryContract.View
    private lateinit var repository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        presenter = OrderHistoryPresenter(
            view = view,
            repository = repository
        )
    }

    @Test
    fun `주문 목록들을 받아온 후 뷰를 초기화한다`() {
        // given
        val orders = OrderFixture.createOrders()

        every {
            repository.getOrders()
        } returns CompletableFuture.completedFuture(Result.success(orders))

        // when 저장소로부터 주문 목록을 받아온다.
        presenter.getOrders()

        // then 뷰가 초기화된다
        verify {
            view.initView(
                orders.map { it.toUiModel() }
            )
        }
    }

    @Test
    fun `주문 목록들을 받아오지 못한 경우 에러 메시지를 띄운다`() {
        // given
        val errorMessage = "주문 목록을 불러올 수 없습니다."

        every {
            repository.getOrders()
        } returns CompletableFuture.completedFuture(Result.failure(Throwable(errorMessage)))

        // when: 저장소로부터 주문 목록을 받아온다.
        presenter.getOrders()

        // then: 주문 목록을 받아오지 못한 경우 에러 메시지를 띄운다.
        verify { view.showErrorMessage(any()) }
    }
}
