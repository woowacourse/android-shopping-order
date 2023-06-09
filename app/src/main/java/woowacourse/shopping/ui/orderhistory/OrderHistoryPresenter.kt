package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.ui.model.mapper.OrderHistoryMapper.toView

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val memberRepository: MemberRepository
) : OrderHistoryContract.Presenter {
    override fun loadHistories() {
        memberRepository.getOrderHistories(
            onSuccess = {histories ->
                view.showHistories(histories.map { it.toView() })
            },
            onFailure = {
                view.notifyFailure(it)
            }
        )

    }

    override fun openDetail(id: Int) {
        view.showDetail(id)
    }
}