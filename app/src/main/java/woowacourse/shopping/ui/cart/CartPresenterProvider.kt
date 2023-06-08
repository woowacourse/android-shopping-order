package woowacourse.shopping.ui.cart

import woowacourse.shopping.data.RepositoryContainer

object CartPresenterProvider {
    fun create(
        view: CartContract.View,
        repositoryContainer: RepositoryContainer,
        pageSize: Int
    ): CartContract.Presenter {
        return CartPresenter(
            view,
            repositoryContainer.cartItemRepository,
            repositoryContainer.orderRepository,
            repositoryContainer.userRepository,
            pageSize
        )
    }
}
