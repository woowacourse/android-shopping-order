package woowacourse.shopping.ui.order.orderlist

import android.content.Context
import woowacourse.shopping.data.repository.RepositoryContainer

object OrderListPresenterProvider {
    fun create(
        view: OrderListContract.View,
        context: Context
    ): OrderListContract.Presenter {
        val repositoryContainer = RepositoryContainer.getInstance(context)
        return OrderListPresenter(
            view, repositoryContainer.orderRepository
        )
    }
}
