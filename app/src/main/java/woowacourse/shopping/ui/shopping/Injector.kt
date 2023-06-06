package woowacourse.shopping.ui.shopping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.cart.DefaultCartRemoteDataSource
import woowacourse.shopping.data.cart.DefaultCartRepository
import woowacourse.shopping.data.database.ShoppingDBOpenHelper
import woowacourse.shopping.data.database.dao.RecentProductDao
import woowacourse.shopping.data.member.DefaultMemberRemoteDataSource
import woowacourse.shopping.data.member.DefaultMemberRepository
import woowacourse.shopping.data.order.DefaultOrderRemoteDataSource
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.product.DefaultProductRemoteDataSource
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.data.recentproduct.DefaultRecentProductRepository
import woowacourse.shopping.data.server.ShoppingRetrofit

object RepositoryInjector {
    fun injectProductRepository(retrofit: Retrofit) =
        DefaultProductRepository(
            productRemoteDataSource = DefaultProductRemoteDataSource(retrofit),
            cartRemoteDataSource = DefaultCartRemoteDataSource(retrofit)
        )

    fun injectRecentProductRepository(
        database: SQLiteDatabase,
        server: String,
        retrofit: Retrofit
    ) = DefaultRecentProductRepository(
        recentProductDao = RecentProductDao(database, server),
        productRemoteDataSource = DefaultProductRemoteDataSource(retrofit)
    )

    fun injectCartRepository(retrofit: Retrofit) =
        DefaultCartRepository(DefaultCartRemoteDataSource(retrofit))

    fun injectMemberRepository(retrofit: Retrofit) =
        DefaultMemberRepository(DefaultMemberRemoteDataSource(retrofit))

    fun injectOrderRepository(retrofit: Retrofit) =
        DefaultOrderRepository(DefaultOrderRemoteDataSource(retrofit))
}

object DatabaseInjector {
    fun inject(context: Context): SQLiteDatabase =
        ShoppingDBOpenHelper(context.applicationContext).writableDatabase
}

object RetrofitInjector {
    fun inject(context: Context): Retrofit {
        val storage = Storage.getInstance(context.applicationContext)
        return ShoppingRetrofit.getInstance(storage)
    }
}
