package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.data.ShoppingRetrofit
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.OrderRemoteSource
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource

object OrderListPresenterProvider {
    fun create(view: OrderListContract.View): OrderListContract.Presenter {
        return OrderListPresenter(
            view,
            DefaultOrderRepository(OrderRemoteSource(ShoppingRetrofit.retrofit)),
            DefaultUserRepository(UserMemorySource(), UserRemoteSource(ShoppingRetrofit.retrofit))
        )
    }
}
