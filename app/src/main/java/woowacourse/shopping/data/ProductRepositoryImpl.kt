package woowacourse.shopping.data

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.data.model.response.ProductContent
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override fun loadProductsUpToPage(
        pageIndex: Int,
        pageSize: Int,
        callback: (List<Product>, Boolean) -> Unit,
    ) {
        val sizeToLoad = pageSize * (pageIndex + 1)
        productDataSource.fetchPageOfProducts(
            pageIndex = 0,
            pageSize = sizeToLoad,
        ) { response ->
            val products: List<Product> = response.productContent.map(ProductContent::toProduct)
            val isLastPage = products.size < sizeToLoad
            callback(products, isLastPage)
        }
    }

    override fun findProductById(
        id: Long,
        callback: (Product?) -> Unit,
    ) {
        productDataSource.fetchProduct(id) { response ->
            val product = response.toProduct()
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
            val recentProducts = recentEntities.map { entity -> entity.toProduct() }
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

    override fun loadRecommendedProducts(
        count: Int,
        callback: (List<Product>) -> Unit,
    ) {
        getMostRecentProduct { mostRecentProduct ->
            val recommendedCategory = mostRecentProduct?.category
            if (recommendedCategory != null) {
                loadAllCartItems { cartItems ->
                    loadProductsByCategory(recommendedCategory) { productsInCategory ->
                        val productsInCart = cartItems.map(CartItem::product).toSet()
                        val recommendedProducts =
                            productsInCategory.filterNot { product -> product in productsInCart }
                        callback(recommendedProducts.take(count))
                    }
                }
            }
        }
    }

    override fun loadProductsByCategory(
        category: String,
        callback: (List<Product>) -> Unit,
    ) {
        productDataSource.fetchPageOfProducts(
            pageIndex = 0,
            pageSize = Int.MAX_VALUE,
        ) { response ->
            val products: List<Product> =
                response.productContent.map { content -> content.toProduct() }
            val filteredProducts = products.filter { product -> product.category == category }
            callback(filteredProducts)
        }
    }
}
