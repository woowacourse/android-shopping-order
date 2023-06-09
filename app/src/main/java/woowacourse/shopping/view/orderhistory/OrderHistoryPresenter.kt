package woowacourse.shopping.view.orderhistory

import com.shopping.repository.MemberRepository
import woowacourse.shopping.model.uimodel.mapper.toUIModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    memberRepository: MemberRepository
) : OrderHistoryContract.Presenter {

    init {
        memberRepository.getOrderHistories(
            onSuccess = { orderHistory ->
                view.updateOrders(orderHistory.map { it.toUIModel() })
            }
        )
    }
}
