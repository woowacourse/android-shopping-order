package woowacourse.shopping.orderhistory

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import woowacourse.shopping.createOrderHistory
import woowacourse.shopping.createOrderHistoryModel
import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.ui.model.OrderHistoryModel
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract
import woowacourse.shopping.ui.orderhistory.OrderHistoryPresenter

class OrderHistoryPresenterTest {
    private val view: OrderHistoryContract.View = mockk()
    private val memberRepository: MemberRepository = mockk()
    private lateinit var presenter: OrderHistoryContract.Presenter

    @Test
    fun `주문 목록을 노출한다`() {
        // given
        presenter = OrderHistoryPresenter(view, memberRepository)
        every { view.showHistories(any()) } just runs

        val histories: List<OrderHistory> = listOf(createOrderHistory(id = 1), createOrderHistory(id = 2))
        val successSlot = slot<(List<OrderHistory>) -> Unit>()
        every { memberRepository.getHistories(capture(successSlot), any()) } answers {
            successSlot.captured(histories)
        }

        // when
        presenter.loadHistories()

        // then
        val expected: List<OrderHistoryModel> = listOf(createOrderHistoryModel(id = 1), createOrderHistoryModel(id = 2))
        verify { view.showHistories(expected) }
    }
}