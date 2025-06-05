package woowacourse.shopping.di.provider

import woowacourse.shopping.data.service.CartService
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.service.ProductService

object ServiceProvider {
    fun provideProduceService(): ProductService = RetrofitClient.instance.create(ProductService::class.java)

    fun provideCartService(): CartService = RetrofitClient.instance.create(CartService::class.java)

    fun provideOrderService(): OrderService = RetrofitClient.instance.create(OrderService::class.java)
}
