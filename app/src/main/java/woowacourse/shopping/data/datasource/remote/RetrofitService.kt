
package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.ShoppingApplication.Companion.tokenPreference
import woowacourse.shopping.data.datasource.remote.cart.CartService
import woowacourse.shopping.data.datasource.remote.order.OrderService
import woowacourse.shopping.data.datasource.remote.point.PointService
import woowacourse.shopping.data.datasource.remote.product.ProductService

object RetrofitService {

    private val token = tokenPreference.getToken("") ?: ""

    val productService: ProductService =
        RetrofitClient.retrofit.create(ProductService::class.java)
    val pointService: PointService =
        RetrofitClient.getInstanceWithToken(token).create(PointService::class.java)
    val cartService: CartService =
        RetrofitClient.getInstanceWithToken(token).create(CartService::class.java)
    val orderService: OrderService =
        RetrofitClient.getInstanceWithToken(token).create(OrderService::class.java)
}
