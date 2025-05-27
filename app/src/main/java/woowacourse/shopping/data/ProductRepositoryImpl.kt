package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.CartDao
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val products: List<Product>,
    private val cartDao: CartDao,
    private val recentProductDao: RecentProductDao,
    private val productDataSource: ProductDataSource,
    ) : ProductRepository {
    override fun loadProducts(
        page: Int,
        loadSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    ) {
        productDataSource.fetchProducts(
            page = page,
            size = loadSize,
        ){
            val products: List<Product> = it.content.map { it.toProduct() }
            val hasMore: Boolean = it.last

            callback(products, hasMore)
        }
//        val products = products.filter { it.id > lastItemId }.take(loadSize)
//        val lastId = products.lastOrNull()?.id ?: return callback(products, false)
//
//        val hasMore = this.products.any { it.id > lastId }
//
//        callback(products, hasMore)
    }
    override fun getProductById(
        id: Long,
        callback: (Product?) -> Unit,
    ) {
//        thread {
//            val product = products.find { it.id == id }
//            callback(product)
//        }

        productDataSource.fetchProduct(id){
            val product = it.toProduct()
            callback(product)
        }
    }

    override fun loadCartItems(callback: (List<CartItem>?) -> Unit) {
        thread {
            val cartItems = cartDao.getCartItems().map { it.toCartItem() }
            callback(cartItems)
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

            val recentProducts =
                recentEntities.mapNotNull { entity ->
                    products.find { it.id == entity.productId }
                }

            callback(recentProducts)
        }
    }

    override fun loadLastViewedProduct(
        currentProductId: Long,
        callback: (Product?) -> Unit,
    ) {
        thread {
            val entity = recentProductDao.getLastViewedProduct(currentProductId)

            val product =
                entity?.let {
                    products.find { it.id == entity.productId }
                }
            callback(product)
        }
    }


}
