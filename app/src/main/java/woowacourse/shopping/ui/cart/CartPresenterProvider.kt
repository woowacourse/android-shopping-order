package woowacourse.shopping.ui.cart

import android.content.Context
import woowacourse.shopping.data.repository.RepositoryContainer

object CartPresenterProvider {
    fun create(
        view: CartContract.View,
        context: Context,
        pageSize: Int
    ): CartContract.Presenter {
        val repositoryContainer = RepositoryContainer.getInstance(context)
        return CartPresenter(
            view,
            repositoryContainer.cartItemRepository,
            repositoryContainer.orderRepository,
            pageSize
        )
    }
}
