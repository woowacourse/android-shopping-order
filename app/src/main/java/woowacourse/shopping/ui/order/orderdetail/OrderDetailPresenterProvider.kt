package woowacourse.shopping.ui.order.orderdetail

import android.content.Context
import woowacourse.shopping.data.repository.RepositoryContainer

object OrderDetailPresenterProvider {
    fun create(
        view: OrderDetailContract.View,
        context: Context
    ): OrderDetailContract.Presenter {
        val repositoryContainer = RepositoryContainer.getInstance(context)

        return OrderDetailPresenter(
            view, repositoryContainer.orderRepository, repositoryContainer.userRepository
        )
    }
}
