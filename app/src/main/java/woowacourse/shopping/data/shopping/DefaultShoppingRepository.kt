package woowacourse.shopping.data.shopping

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.data.shopping.product.datasource.ProductDataSource
import woowacourse.shopping.data.shopping.recent.RecentProductData
import woowacourse.shopping.data.shopping.recent.RecentProductDataSource
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.ShoppingRepository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class DefaultShoppingRepository(
    private val productDataSource: ProductDataSource,
    private val recentProductDataSource: RecentProductDataSource,
) : ShoppingRepository {
    private val cachedPagingProducts = ConcurrentHashMap<Int, List<Product>>()
    private val cachedProductsById = ConcurrentHashMap<Long, Product>()
    private var cachedPageData: ProductPageData? = null

    override fun products(
        currentPage: Int,
        size: Int,
    ): Result<List<Product>> {
        val products = cachedPagingProducts[currentPage]
        if (products != null) return Result.success(products)
        return productDataSource.products(currentPage, size)
            .mapCatching {
                cachedPageData = it
                cachedPagingProducts[currentPage] = it.products
                it.products.map { product ->
                    cachedProductsById[product.id] = product
                }
                it.products
            }
    }

    override fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<List<Product>> {
        return productDataSource.products(category, currentPage, size)
            .mapCatching { it.products }
    }

    override fun productById(id: Long): Result<Product> {
        val cachedProduct = cachedProductsById[id] ?: return productDataSource.fetchProductById(id)
        return Result.success(cachedProduct)
    }

    override fun canLoadMore(
        page: Int,
        size: Int,
    ): Result<Boolean> {
        val totalProductSize =
            cachedPageData?.totalProductSize ?: return Result.failure(Exception("No data"))
        val canLoadMore = totalProductSize > page * size
        return Result.success(canLoadMore)
    }

    override fun recentProducts(size: Int): Result<List<Product>> {
        val result = recentProductDataSource.recentProducts(size)
        return result.mapCatching {
            it.map { product ->
                val cachedProduct = cachedProductsById[product.productId]
                if (cachedProduct != null) return@map cachedProduct
                val productResult = productDataSource.fetchProductById(product.productId)
                if (productResult.isFailure) error("Product(id=${product.productId}) not found")
                productResult.getOrThrow()
            }
        }
    }

    override fun saveRecentProduct(id: Long): Result<Long> {
        return recentProductDataSource.saveRecentProduct(RecentProductData(id, LocalDateTime.now()))
    }
}
