package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override fun loadProducts(
        page: Int,
        loadSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    ) {
        productDataSource.fetchProducts(
            page = page,
            size = loadSize,
        ) {
            val products: List<Product> = it.productContent.map { it.toProduct() }
            val hasMore = products.size >= loadSize

            callback(products, hasMore)
        }
    }

    override fun getProductById(
        id: Long,
        callback: (Product?) -> Unit,
    ) {
        productDataSource.fetchProduct(id) {
            val product = it.toProduct()
            callback(product)
        }
    }

    override fun loadCartItems(callback: (List<CartItem>?) -> Unit) {
        cartItemDataSource.fetchCartItems(
            page = 0,
            size = Int.MAX_VALUE,
        ) {
            callback(it?.content?.map { it.toCartItem() } ?: emptyList())
        }
    }

    override fun addRecentProduct(product: Product) {
        thread {
            recentProductDao.insert(product.toRecentEntity())
        }
    }

    override fun loadRecentProducts(
        limit: Int,
        callback: (List<Product>) -> Unit,
    ) {
        thread {
            val recentEntities = recentProductDao.getRecentProducts(limit)
            val recentProducts = recentEntities.map { it.toProduct() }
            callback(recentProducts)
        }
    }

    override fun loadLastViewedProduct(
        currentProductId: Long,
        callback: (Product?) -> Unit,
    ) {
        thread {
            val entity = recentProductDao.getLastViewedProduct(currentProductId)
            val product = entity?.toProduct()
            callback(product)
        }
    }
}
