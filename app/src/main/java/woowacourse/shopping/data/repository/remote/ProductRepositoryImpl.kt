package woowacourse.shopping.data.repository.remote

import com.example.domain.cache.ProductCache
import com.example.domain.cache.ProductLocalCache
import com.example.domain.model.CustomError
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.service.product.ProductRemoteService

class ProductRepositoryImpl(
    private val service: ProductRemoteService,
    override val cache: ProductCache = ProductLocalCache,
) : ProductRepository {
    private val products: MutableList<Product> = mutableListOf()
    override fun fetchFirstProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        if (cache.productList.isEmpty()) {
            service.request(
                onSuccess = {
                    products.clear()
                    products.addAll(it)
                    val nextProducts = products.take(20)
                    cache.addProducts(nextProducts)
                    onSuccess(nextProducts)
                },
                onFailure = onFailure,
            )
        } else {
            onSuccess(cache.productList)
        }
    }

    override fun fetchNextProducts(
        lastProductId: Long,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        val nextProducts = products.filter { it.id > lastProductId }.take(20)
        cache.addProducts(nextProducts)
        onSuccess(nextProducts)
    }

    override fun fetchProductById(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        service.requestProduct(productId, onSuccess, onFailure)
    }

    override fun resetCache() {
        cache.clear()
    }
}
