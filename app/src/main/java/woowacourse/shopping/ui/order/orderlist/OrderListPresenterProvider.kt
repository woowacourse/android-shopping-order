package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.data.RepositoryContainer

object OrderListPresenterProvider {
    fun create(
        view: OrderListContract.View,
        repositoryContainer: RepositoryContainer
    ): OrderListContract.Presenter {
        return OrderListPresenter(
            view, repositoryContainer.orderRepository, repositoryContainer.userRepository
        )
    }
}
