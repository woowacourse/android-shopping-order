package woowacourse.shopping.util

import android.content.Context
import com.example.domain.cart.CartRepository
import com.example.domain.product.ProductRepository
import com.example.domain.product.recent.RecentProductRepository
import retrofit2.Retrofit
import woowacourse.shopping.data.cart.CartRemoteRepository
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.data.product.ProductRemoteRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.util.RetrofitManager

object Injector {
    private var serverUrl: String? = null
    private val retrofit: Retrofit by lazy { RetrofitManager.getInstance(serverUrl!!).retrofit }

    fun provideCartRepository(serverUrl: String): CartRepository {
        if (this.serverUrl == null) this.serverUrl = serverUrl
        return CartRemoteRepository(retrofit)
    }

    fun provideProductRepository(serverUrl: String): ProductRepository {
        if (this.serverUrl == null) this.serverUrl = serverUrl
        return ProductRemoteRepository(retrofit)
    }

    fun provideRecentProductRepository(context: Context, serverUrl: String): RecentProductRepository {
        if (this.serverUrl == null) this.serverUrl = serverUrl
        return RecentProductRepositoryImpl(context, serverUrl)
    }

    fun provideOrderRemoteRepository(serverUrl: String): OrderRemoteRepository {
        if (this.serverUrl == null) this.serverUrl = serverUrl
        return OrderRemoteRepository(retrofit)
    }
}
