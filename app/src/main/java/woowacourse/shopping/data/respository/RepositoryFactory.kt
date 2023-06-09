package woowacourse.shopping.data.respository

import android.content.Context
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.card.CardDAO
import woowacourse.shopping.data.respository.card.CardRepositoryImpl
import woowacourse.shopping.data.respository.cart.CartRepositoryImpl
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.respository.product.ProductRepositoryImpl
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.respository.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.respository.recentproduct.source.local.RecentProductLocalDataSourceImpl

class RepositoryFactory private constructor(context: Context, url: Server.Url) {
    private val retrofitBuilder = RetrofitBuilder.getInstance(context, url)

    private val cardDAO = CardDAO
    private val cartLocalDataSource = CartLocalDataSourceImpl(context, url)
    private val recentProductLocalDataSource = RecentProductLocalDataSourceImpl(context, url)

    private val cartRemoteDataSource = CartRemoteDataSourceImpl(retrofitBuilder.createCartService())
    private val orderRemoteDataSource = OrderRemoteDataSourceImpl(retrofitBuilder.createOrderService())
    private val productRemoteDataSource = ProductRemoteDataSourceImpl(retrofitBuilder.createProductService())

    val cardRepository = CardRepositoryImpl(cardDAO)
    val cartRepository = CartRepositoryImpl(cartLocalDataSource, cartRemoteDataSource)
    val orderRepository = OrderRepositoryImpl(orderRemoteDataSource)
    val productRepository = ProductRepositoryImpl(productRemoteDataSource)
    val recentProductRepository = RecentProductRepositoryImpl(productRemoteDataSource, recentProductLocalDataSource)

    companion object {
        @Volatile
        private var instance: RepositoryFactory? = null

        fun getInstance(context: Context, url: Server.Url): RepositoryFactory {
            synchronized(this) {
                instance?.let { return it }
                return RepositoryFactory(context, url)
            }
        }
    }
}
