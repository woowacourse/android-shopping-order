package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.db.RecentProductDao
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.mapper.toCartItem
import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.mapper.toRecentEntity
import woowacourse.shopping.data.model.response.product.ProductContent
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
        val response =
            productDataSource.fetchPageOfProducts(
                pageIndex = 0,
                pageSize = sizeToLoad,
            )
        val products = response.productContent.map(ProductContent::toProduct)
        val isLast = products.size < sizeToLoad
        return Page(products, response.first, isLast)
    }

    override suspend fun findProductById(id: Long): Product {
        return productDataSource.fetchProduct(id).toProduct()
    }

    override suspend fun loadAllCartItems(): List<CartItem> {
        val response =
            cartItemDataSource.fetchPageOfCartItems(
                pageIndex = 0,
                pageSize = Int.MAX_VALUE,
            )
        return response.content.map { content -> content.toCartItem() }
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
            ).productContent.map(ProductContent::toProduct)
        return products.filter { product -> product.category == category }
    }
}
