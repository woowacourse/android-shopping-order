package woowacourse.shopping.ui.shopping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
    fun injectProductRepository(retrofit: ShoppingRetrofit) =
        DefaultProductRepository(
            productRemoteDataSource = DefaultProductRemoteDataSource(retrofit.productService),
            cartRemoteDataSource = DefaultCartRemoteDataSource(retrofit.cartService)
        )

    fun injectRecentProductRepository(
        database: SQLiteDatabase,
        server: String,
        retrofit: ShoppingRetrofit
    ) = DefaultRecentProductRepository(
        recentProductDao = RecentProductDao(database, server),
        productRemoteDataSource = DefaultProductRemoteDataSource(retrofit.productService)
    )

    fun injectCartRepository(retrofit: ShoppingRetrofit) =
        DefaultCartRepository(DefaultCartRemoteDataSource(retrofit.cartService))

    fun injectMemberRepository(retrofit: ShoppingRetrofit) =
        DefaultMemberRepository(DefaultMemberRemoteDataSource(retrofit.memberService))

    fun injectOrderRepository(retrofit: ShoppingRetrofit) =
        DefaultOrderRepository(DefaultOrderRemoteDataSource(retrofit.orderService))
}

object DatabaseInjector {
    fun inject(context: Context): SQLiteDatabase =
        ShoppingDBOpenHelper(context.applicationContext).writableDatabase
}

object RetrofitInjector {
    fun inject(context: Context): ShoppingRetrofit {
        val storage = Storage.getInstance(context.applicationContext)
        return ShoppingRetrofit.getInstance(storage)
    }
}
