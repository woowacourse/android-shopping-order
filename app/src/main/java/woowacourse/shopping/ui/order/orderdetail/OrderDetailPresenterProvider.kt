package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.RepositoryContainer

object OrderDetailPresenterProvider {
    fun create(
        view: OrderDetailContract.View,
        repositoryContainer: RepositoryContainer
    ): OrderDetailContract.Presenter {
        return OrderDetailPresenter(
            view, repositoryContainer.orderRepository, repositoryContainer.userRepository
        )
    }
}
