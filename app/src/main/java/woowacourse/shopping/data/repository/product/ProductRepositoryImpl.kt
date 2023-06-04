package woowacourse.shopping.data.repository.product

import android.util.Log
import com.example.domain.ProductCache
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.datasource.remote.product.ProductRemoteDataSource
import java.lang.Thread.sleep

class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
    private val productCache: ProductCache
) : ProductRepository {
    override fun getProducts(
        page: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        if (productCache.productList.isEmpty()) {
            remoteDataSource.requestProducts(
                onSuccess = {
                    productCache.addProducts(it)
                    sleep(2000)
                    onSuccess(productCache.getSubProducts(1, LOAD_SIZE))
                },
                onFailure = { Log.d("Hash", "failure: $it") }
            )
        } else {
            onSuccess(productCache.getSubProducts(page, LOAD_SIZE))
        }
    }

    override fun getProductById(id: Long): Product? {
        return productCache.productList.find { id == it.id }
    }

    override fun clearCache() {
        productCache.clear()
    }

    companion object {
        private const val LOAD_SIZE = 20
    }
}
