package woowacourse.shopping.ui.productdetail

import android.content.Context
import woowacourse.shopping.data.repository.RepositoryContainer

object ProductDetailPresenterProvider {
    fun create(
        view: ProductDetailContract.View,
        context: Context
    ): ProductDetailContract.Presenter {
        val repositoryContainer = RepositoryContainer.getInstance(context)
        return ProductDetailPresenter(
            view,
            repositoryContainer.productRepository,
            repositoryContainer.cartItemRepository,
            repositoryContainer.userRepository,
            repositoryContainer.recentlyViewedProductRepository,
        )
    }
}
