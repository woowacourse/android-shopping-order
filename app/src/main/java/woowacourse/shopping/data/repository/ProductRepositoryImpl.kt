package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page
import woowacourse.shopping.domain.Product

class ProductRepositoryImpl(
    private val recentProductDao: RecentProductDao,
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override suspend fun loadProductsUpToPage(
        pageIndex: Int,
        pageSize: Int,
    ): Page<Product> {
        val sizeToLoad = pageSize * (pageIndex + 1)
        val page =
            productDataSource.fetchPageOfProducts(
                pageIndex = 0,
                pageSize = sizeToLoad,
            )
        val isLast = page.items.size < sizeToLoad
        return page.copy(isLast = isLast)
    }

    override suspend fun loadProductById(id: Long): Product {
        val product = productDataSource.fetchProduct(id)
        addRecentProduct(product)
        return product
    }

    override suspend fun loadAllCartItems(): List<CartItem> {
        return cartItemDataSource.fetchPageOfCartItems(0, Int.MAX_VALUE).items
    }

    override suspend fun addRecentProduct(product: Product) {
        recentProductDao.insert(product.toRecentEntity())
    }

    override suspend fun loadRecentProducts(count: Int): List<Product> {
        return recentProductDao.getRecentProducts(count).map(RecentProductEntity::toProduct)
    }

    override suspend fun getMostRecentProduct(): Product? {
        return recentProductDao.getMostRecentProduct()?.toProduct()
    }

    override suspend fun loadRecommendedProducts(count: Int): List<Product> {
        getMostRecentProduct()?.category?.let { category ->
            val cart = loadAllCartItems().map(CartItem::product).toSet()
            return loadProductsByCategory(category)
                .filterNot { product -> product in cart }
                .take(count)
        }
        return emptyList()
    }

    override suspend fun loadProductsByCategory(category: String): List<Product> {
        val products =
            productDataSource.fetchPageOfProducts(
                pageIndex = 0,
                pageSize = Int.MAX_VALUE,
            ).items
        return products.filter { product -> product.category == category }
    }
}
