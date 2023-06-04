package woowacourse.shopping.data.remote

import woowacourse.shopping.data.remote.api.ProductDetailService
import woowacourse.shopping.data.remote.api.ProductService
import woowacourse.shopping.data.remote.api.ShoppingCartService

object ServiceFactory {
    val productService = NetworkModule.create<ProductService>()
    val productDetailService = NetworkModule.create<ProductDetailService>()
    val shoppingCartService = NetworkModule.create<ShoppingCartService>()
}
