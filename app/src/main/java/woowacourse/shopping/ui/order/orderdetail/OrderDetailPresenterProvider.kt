package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.network.ShoppingRetrofit
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.OrderRemoteSource
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource

object OrderDetailPresenterProvider {
    fun create(view: OrderDetailContract.View): OrderDetailContract.Presenter {
        return OrderDetailPresenter(
            view,
            DefaultOrderRepository(OrderRemoteSource(ShoppingRetrofit.retrofit)),
            DefaultUserRepository(UserMemorySource(), UserRemoteSource(ShoppingRetrofit.retrofit))
        )
    }
}
