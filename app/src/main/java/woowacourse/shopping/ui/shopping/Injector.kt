package woowacourse.shopping.ui.shopping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.cart.CartRemoteDataSourceRetrofit
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.database.ShoppingDBOpenHelper
import woowacourse.shopping.data.database.dao.RecentProductDao
import woowacourse.shopping.data.member.MemberRemoteDataSourceRetrofit
import woowacourse.shopping.data.member.MemberRepositoryImpl
import woowacourse.shopping.data.order.OrderRemoteDataSourceRetrofit
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.product.ProductRemoteDataSourceRetrofit
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.server.ShoppingRetrofit

object RepositoryInjector {
    fun injectProductRepository(retrofit: Retrofit) =
        ProductRepositoryImpl(
            productRemoteDataSource = ProductRemoteDataSourceRetrofit(retrofit),
            cartRemoteDataSource = CartRemoteDataSourceRetrofit(retrofit)
        )

    fun injectRecentProductRepository(
        database: SQLiteDatabase,
        server: String,
        retrofit: Retrofit
    ) =
        RecentProductRepositoryImpl(
            recentProductDao = RecentProductDao(database, server),
            productRemoteDataSource = ProductRemoteDataSourceRetrofit(retrofit)
        )

    fun injectCartRepository(retrofit: Retrofit) =
        CartRepositoryImpl(CartRemoteDataSourceRetrofit(retrofit))

    fun injectMemberRepository(retrofit: Retrofit) =
        MemberRepositoryImpl(MemberRemoteDataSourceRetrofit(retrofit))

    fun injectOrderRepository(retrofit: Retrofit) =
        OrderRepositoryImpl(OrderRemoteDataSourceRetrofit(retrofit))
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
