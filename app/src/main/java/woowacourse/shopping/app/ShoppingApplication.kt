package woowacourse.shopping.app

import android.app.Application
import androidx.preference.PreferenceManager
import woowacourse.shopping.data.repsoitory.OrderRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductHistoryRepositoryImpl
import woowacourse.shopping.data.repsoitory.ProductRepositoryImpl
import woowacourse.shopping.data.repsoitory.ShoppingCartRepositoryImpl
import woowacourse.shopping.local.datasource.ProductHistoryDataSourceImpl
import woowacourse.shopping.local.db.ProductHistoryDatabase
import woowacourse.shopping.local.provider.AuthProviderImpl
import woowacourse.shopping.remote.api.BaseUrl
import woowacourse.shopping.remote.api.NetworkModule
import woowacourse.shopping.remote.datasource.OrderDataSourceImpl
import woowacourse.shopping.remote.datasource.ProductDataSourceImpl
import woowacourse.shopping.remote.datasource.ShoppingCartDataSourceImpl

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAuthProvider()
        initNetworkModule()
        initDataSources()
        initRepositories()
    }

    private fun initAuthProvider() {
        AuthProviderImpl.setInstance(
            sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext),
        )
    }

    private fun initNetworkModule() {
        NetworkModule.setInstance(
            baseUrl = BaseUrl(BASE_URL),
            authProvider = AuthProviderImpl.getInstance(),
        )
    }

    private fun initDataSources() {
        ShoppingCartDataSourceImpl.setInstance(cartService = NetworkModule.getInstance().cartService)
        ProductDataSourceImpl.setInstance(productService = NetworkModule.getInstance().productService)
        OrderDataSourceImpl.setInstance(orderService = NetworkModule.getInstance().orderService)
        ProductHistoryDataSourceImpl.setInstance(
            productHistoryDao = ProductHistoryDatabase.getDatabase(applicationContext).dao(),
        )
    }

    private fun initRepositories() {
        ShoppingCartRepositoryImpl.setInstance(dataSource = ShoppingCartDataSourceImpl.getInstance())
        ProductRepositoryImpl.setInstance(
            productDataSource = ProductDataSourceImpl.getInstance(),
            shoppingCartDataSource = ShoppingCartDataSourceImpl.getInstance(),
        )
        OrderRepositoryImpl.setInstance(orderDataSource = OrderDataSourceImpl.getInstance())
        ProductHistoryRepositoryImpl.setInstance(
            productHistoryDataSource = ProductHistoryDataSourceImpl.getInstance(),
            shoppingCartDataSource = ShoppingCartDataSourceImpl.getInstance(),
        )
    }

    companion object {
        const val BASE_URL = "http://54.180.95.212:8080"
    }
}
