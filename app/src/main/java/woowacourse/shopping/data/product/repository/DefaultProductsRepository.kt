package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.source.ProductsDataSource
import woowacourse.shopping.data.product.source.RemoteProductsDataSource
import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product
import kotlin.concurrent.thread

class DefaultProductsRepository(
    private val productsDataSource: ProductsDataSource = RemoteProductsDataSource(),
//    private val recentViewedProductsDataSource: RecentViewedProductsDataSource = LocalRecentViewedProductsDataSource,
) : ProductsRepository {
    override fun loadPageableProducts(
        page: Int,
        size: Int,
        onLoad: (Result<PageableProducts>) -> Unit,
    ) {
        {
            val pageableProductData: PageableProductData =
                productsDataSource.pageableProducts(
                    page = page,
                    size = size,
                )
            PageableProducts(
                products = pageableProductData.products.map { it.toDomain() },
                loadable = pageableProductData.loadable,
            )
        }.runAsync(onLoad)
    }

    override fun getProductById(
        id: Long,
        onLoad: (Result<Product?>) -> Unit,
    ) {
        {
            val productEntity: ProductEntity? = productsDataSource.getProductById(id)
            productEntity?.toDomain()
        }.runAsync(onLoad)
    }
//    override fun loadLatestViewedProduct(onLoad: (Result<Product?>) -> Unit) {
//        {
//            val latestViewedProductId: Long? =
//                recentViewedProductsDataSource
//                    .load()
//                    .maxByOrNull { it.viewedAt }
//                    ?.productId
//            if (latestViewedProductId == null) {
//                null
//            } else {
//                productsDataSource.getProductById(latestViewedProductId)?.toDomain()
//            }
//        }.runAsync(onLoad)
//    }
//
//    override fun loadLastViewedProducts(onLoad: (Result<List<Product>>) -> Unit) {
//        {
//            recentViewedProductsDataSource
//                .load()
//                .sortedByDescending { it.viewedAt }
//                .mapNotNull { productsDataSource.getProductById(it.productId)?.toDomain() }
//        }.runAsync(onLoad)
//    }

    //    override fun recordViewedProduct(
//        product: Product,
//        onLoad: (Result<Unit>) -> Unit,
//    ) {
//        {
//            recentViewedProductsDataSource.upsert(
//                RecentViewedProductEntity(product.id, LocalDateTime.now()),
//            )
//        }.runAsync(onLoad)
//    }
    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }
}
