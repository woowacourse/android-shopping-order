package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.api.OrderCompleteService
import woowacourse.shopping.data.remote.api.OrderHistoryService
import woowacourse.shopping.data.remote.api.OrderService
import woowacourse.shopping.data.remote.api.ProductDetailService
import woowacourse.shopping.data.remote.api.ShoppingCartService
import woowacourse.shopping.data.remote.api.ShoppingService

object ServiceFactory {
    val shoppingService = NetworkModuleUsingGson.create<ShoppingService>()
    val productDetailService = NetworkModuleUsingGson.create<ProductDetailService>()
    val shoppingCartService = NetworkModuleUsingGson.create<ShoppingCartService>()
    val orderService = NetworkModuleUsingSerialization.createNew<OrderService>()
    val orderCompleteService = NetworkModuleUsingSerialization.createNew<OrderCompleteService>()
    val orderHistoryService = NetworkModuleUsingSerialization.createNew<OrderHistoryService>()
}
