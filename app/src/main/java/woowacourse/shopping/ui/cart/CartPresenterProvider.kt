package woowacourse.shopping.ui.cart

import woowacourse.shopping.data.ShoppingRetrofit
import woowacourse.shopping.data.cart.CartItemRemoteSource
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.OrderRemoteSource
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource

object CartPresenterProvider {
    fun create(view: CartContract.View, pageSize: Int): CartContract.Presenter {
        return CartPresenter(
            view,
            DefaultCartItemRepository(
                CartItemRemoteSource(ShoppingRetrofit.retrofit)
            ),
            DefaultOrderRepository(
                OrderRemoteSource(ShoppingRetrofit.retrofit)
            ),
            DefaultUserRepository(
                UserMemorySource(), UserRemoteSource()
            ),
            pageSize
        )
    }
}
