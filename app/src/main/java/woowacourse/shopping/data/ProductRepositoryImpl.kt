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
    override fun loadPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    ) {
        productDataSource.fetchPageOfProducts(
            pageIndex = pageIndex,
            pageSize = pageSize,
        ) {
            val products: List<Product> = it.productContent.map { it.toProduct() }
            val hasMore = products.size >= pageSize

            callback(products, hasMore)
        }
    }

    override fun findProductById(
        id: Long,
        callback: (Product?) -> Unit,
    ) {
        productDataSource.fetchProduct(id) {
            val product = it.toProduct()
            callback(product)
        }
    }

    override fun loadAllCartItems(callback: (List<CartItem>) -> Unit) {
        cartItemDataSource.fetchPageOfCartItems(
            pageIndex = 0,
            pageSize = Int.MAX_VALUE,
        ) { cartItems ->
            callback(cartItems?.content?.map { content -> content.toCartItem() }.orEmpty())
        }
    }

    override fun addRecentProduct(product: Product) {
        thread {
            recentProductDao.insert(product.toRecentEntity())
        }
    }

    override fun loadRecentProducts(
        count: Int,
        callback: (List<Product>) -> Unit,
    ) {
        thread {
            val recentEntities = recentProductDao.getRecentProducts(count)
            val recentProducts = recentEntities.map { it.toProduct() }
            callback(recentProducts)
        }
    }

    override fun getMostRecentProduct(callback: (Product?) -> Unit) {
        thread {
            val entity = recentProductDao.getMostRecentProduct()
            val product = entity?.toProduct()
            callback(product)
        }
    }

    override fun loadProductsByCategory(
        category: String,
        callback: (List<Product>) -> Unit,
    ) {
        productDataSource.fetchPageOfProducts(
            pageIndex = 0,
            pageSize = Int.MAX_VALUE,
        ) {
            val products: List<Product> = it.productContent.map { it.toProduct() }
            val filteredProducts = products.filter { it.category == category }
            callback(filteredProducts)
        }
    }
}
