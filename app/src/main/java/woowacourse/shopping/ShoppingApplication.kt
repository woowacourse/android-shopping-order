package woowacourse.shopping

import android.app.Application
import com.example.data.datasource.local.room.ShoppingDatabase
import com.example.data.datasource.remote.RemoteCartDataSource
import com.example.data.datasource.remote.RemoteOrderDataSource
import com.example.data.datasource.remote.RemoteProductDataSource
import com.example.data.datasource.remote.retrofit.RetrofitClient
import com.example.data.repository.DefaultCartRepository
import com.example.data.repository.DefaultOrderRepository
import com.example.data.repository.DefaultProductRepository
import com.example.data.repository.DefaultRecentProductRepository
import woowacourse.shopping.presentation.cart.CartViewModelFactory
import woowacourse.shopping.presentation.detail.ProductDetailViewModelFactory
import woowacourse.shopping.presentation.products.ProductsViewModelFactory

class ShoppingApplication : Application() {
    private val db: ShoppingDatabase by lazy {
        ShoppingDatabase.getDatabase(this)
    }
    private val retrofitClient = RetrofitClient()

    private val remoteProductDataSource by lazy {
        RemoteProductDataSource(retrofitClient.productService)
    }
    private val remoteCartDataSource by lazy {
        RemoteCartDataSource(retrofitClient.cartItemService)
    }
    private val remoteOrderDataSource by lazy {
        RemoteOrderDataSource(retrofitClient.orderService)
    }
    private val defaultProductRepository by lazy {
        DefaultProductRepository(remoteProductDataSource)
    }
    private val defaultRecentProductRepository by lazy {
        DefaultRecentProductRepository(db.recentProductDao())
    }
    private val defaultCartRepository by lazy {
        DefaultCartRepository(remoteCartDataSource)
    }
    private val defaultOrderRepository by lazy {
        DefaultOrderRepository(remoteOrderDataSource)
    }

    fun getProductsViewModelFactory(): ProductsViewModelFactory =
        ProductsViewModelFactory(
            defaultProductRepository,
            defaultRecentProductRepository,
            defaultCartRepository,
        )

    fun getCartViewModelFactory(): CartViewModelFactory =
        CartViewModelFactory(
            defaultProductRepository,
            defaultRecentProductRepository,
            defaultCartRepository,
            defaultOrderRepository,
        )

    fun getProductDetailViewModelFactory(): ProductDetailViewModelFactory =
        ProductDetailViewModelFactory(
            defaultProductRepository,
            defaultRecentProductRepository,
            defaultCartRepository,
        )
}
