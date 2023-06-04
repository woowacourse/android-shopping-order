package woowacourse.shopping.ui.order.history

import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View

class OrderHistoryPresenter(
    view: View,
    private val orderProductRepository: OrderProductRepository,
) : Presenter(view)
