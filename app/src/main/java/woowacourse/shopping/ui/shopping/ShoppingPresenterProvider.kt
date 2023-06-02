package woowacourse.shopping.ui.shopping

import woowacourse.shopping.RepositoryContainer

object ShoppingPresenterProvider {
    private const val PAGE_SIZE = 20

    fun create(
        view: ShoppingContract.View,
        repositoryContainer: RepositoryContainer
    ): ShoppingContract.Presenter {
        return ShoppingPresenter(
            view,
            repositoryContainer.recentlyViewedProductRepository,
            repositoryContainer.productRepository,
            repositoryContainer.cartItemRepository,
            repositoryContainer.userRepository,
            PAGE_SIZE
        )
    }
}
