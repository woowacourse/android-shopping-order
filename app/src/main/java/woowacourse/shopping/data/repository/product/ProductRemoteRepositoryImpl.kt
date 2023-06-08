package woowacourse.shopping.data.repository.product

import com.example.domain.ProductCache
import com.example.domain.model.product.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.datasource.remote.product.ProductDataSourceImpl
import woowacourse.shopping.data.model.toDomain

class ProductRemoteRepositoryImpl(
    private val service: ProductDataSourceImpl,
    private val productCache: ProductCache
) : ProductRepository {
    override fun getProducts(
        page: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        if (productCache.productList.isEmpty()) {
            service.loadAll(
                onSuccess = { ProductDtos ->
                    productCache.addProducts(ProductDtos.map { it.toDomain() })
                    onSuccess(productCache.getSubProducts(1, LOAD_SIZE))
                },
                onFailure = {}
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
