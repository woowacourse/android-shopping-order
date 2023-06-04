package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.api.ProductDetailService
import woowacourse.shopping.data.remote.api.ShoppingCartService
import woowacourse.shopping.data.remote.api.ShoppingService

object ServiceFactory {
    val shoppingService = NetworkModule.create<ShoppingService>()
    val productDetailService = NetworkModule.create<ProductDetailService>()
    val shoppingCartService = NetworkModule.create<ShoppingCartService>()
}
