package woowacourse.shopping

import android.app.Application
import androidx.room.Room
import com.example.data.datasource.local.room.ShoppingCartDataBase
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

class ShoppingCartApplication : Application() {
    private val db: ShoppingCartDataBase by lazy {
        Room.databaseBuilder(this, ShoppingCartDataBase::class.java, "database").build()
    }
    private val retrofitClient = RetrofitClient()
    private val remoteProductDataSource = RemoteProductDataSource(retrofitClient.productService)
    private val remoteCartDataSource = RemoteCartDataSource(retrofitClient.cartItemService)
    private val remoteOrderDataSource = RemoteOrderDataSource(retrofitClient.orderService)

    private val defaultProductRepository = DefaultProductRepository(remoteProductDataSource)
    private val defaultRecentProductRepository =
        DefaultRecentProductRepository(db.recentProductDao())
    private val defaultCartRepository = DefaultCartRepository(remoteCartDataSource)
    private val defaultOrderRepository = DefaultOrderRepository(remoteOrderDataSource)

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
