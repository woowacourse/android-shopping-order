package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.RepositoryContainer

object ProductDetailPresenterProvider {
    fun create(
        view: ProductDetailContract.View,
        repositoryContainer: RepositoryContainer
    ): ProductDetailContract.Presenter {
        return ProductDetailPresenter(
            view,
            repositoryContainer.productRepository,
            repositoryContainer.cartItemRepository,
            repositoryContainer.userRepository,
            repositoryContainer.recentlyViewedProductRepository,
        )
    }
}
