package woowacourse.shopping.data.product

import com.example.domain.ProductCache
import com.example.domain.datasource.productsDatasource
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository

class ProductMockRepositoryImpl(
    private val productCache: ProductCache = ProductCacheImpl
) : ProductRepository {

    override fun getProducts(page: Int, onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        if (productCache.productList.isEmpty()) {
            productCache.addProducts(productsDatasource)
            onSuccess(productCache.getSubProducts(1, LOAD_SIZE))
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
