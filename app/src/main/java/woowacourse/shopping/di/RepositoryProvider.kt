package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.DefaultRecentProductRepository
import woowacourse.shopping.data.source.local.ShoppingDatabase
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.local.auth.DefaultAuthDataSource
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.ProductRemoteDataSource
import woowacourse.shopping.data.source.remote.RetrofitServices
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryProvider {
    private lateinit var appContext: Context
    private lateinit var database: ShoppingDatabase

    private val retrofitServices: RetrofitServices =
        RetrofitServices(
            baseUrl = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/",
            authToken = "YWtzd29ybnMyMjpwYXNzd29yZA==",
        )

    fun init(context: Context) {
        appContext = context.applicationContext
        database =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "cart-db",
                ).build()
    }

    val productRepository: ProductRepository =
        DefaultProductRepository(
            ProductRemoteDataSource(retrofitServices.productService),
        )

    val recentProductRepository: RecentProductRepository by lazy {
        DefaultRecentProductRepository(
            recentProductDao = database.recentProductDao(),
            productRepository = productRepository,
        )
    }

    val authDataSource: AuthDataSource by lazy {
        DefaultAuthDataSource(appContext)
    }

    val cartRepository: CartRepository by lazy {
        DefaultCartRepository(
            remoteDataSource =
                CartRemoteDataSource(
                    cartService = retrofitServices.cartService,
                ),
        )
    }
}
