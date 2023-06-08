package woowacourse.shopping.ui.shopping

import android.content.Context
import woowacourse.shopping.data.repository.RepositoryContainer

object ShoppingPresenterProvider {

    fun create(
        view: ShoppingContract.View,
        context: Context,
        pageSize: Int
    ): ShoppingContract.Presenter {
        val repositoryContainer = RepositoryContainer.getInstance(context)

        return ShoppingPresenter(
            view,
            repositoryContainer.recentlyViewedProductRepository,
            repositoryContainer.productRepository,
            repositoryContainer.cartItemRepository,
            repositoryContainer.userRepository,
            pageSize
        )
    }
}
