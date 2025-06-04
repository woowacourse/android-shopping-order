package woowacourse.shopping.data.product.repository

import woowacourse.shopping.data.cart.source.CartDataSource
import woowacourse.shopping.data.cart.source.RemoteCartDataSource
import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity
import woowacourse.shopping.data.product.source.LocalRecentViewedProductsDataSource
import woowacourse.shopping.data.product.source.ProductsDataSource
import woowacourse.shopping.data.product.source.RecentViewedProductsDataSource
import woowacourse.shopping.data.product.source.RemoteProductsDataSource
import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.product.Product
import java.time.LocalDateTime
import kotlin.concurrent.thread

class DefaultProductsRepository(
    private val productsDataSource: ProductsDataSource = RemoteProductsDataSource(),
    private val recentViewedProductsDataSource: RecentViewedProductsDataSource = LocalRecentViewedProductsDataSource,
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) : ProductsRepository {
    override fun loadPageableProducts(
        page: Int,
        size: Int,
        onLoad: (Result<Pageable<Product>>) -> Unit,
    ) {
        {
            val pageableProductData: PageableProductData =
                productsDataSource.pageableProducts(
                    page = page,
                    size = size,
                )
            Pageable<Product>(
                items = pageableProductData.products.map { it.toDomain() },
                hasPrevious = false,
                hasNext = pageableProductData.loadable,
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

    override fun loadLatestViewedProduct(onLoad: (productId: Result<Product?>) -> Unit) {
        {
            val productId: Long? =
                recentViewedProductsDataSource.load().maxByOrNull { it.viewedAt }?.productId
            if (productId == null) {
                null
            } else {
                productsDataSource.getProductById(productId)?.toDomain()
            }
        }.runAsync(onLoad)
    }

    override fun loadRecentViewedProducts(onLoad: (Result<List<Product>>) -> Unit) {
        {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { recentViewedProductEntity: RecentViewedProductEntity ->
                    productsDataSource
                        .getProductById(recentViewedProductEntity.productId)
                        ?.toDomain()
                }
        }.runAsync(onLoad)
    }

    override fun addViewedProduct(
        product: Product,
        onLoad: (Result<Unit>) -> Unit,
    ) {
        {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(
                    productId = product.id,
                    viewedAt = LocalDateTime.now(),
                ),
            )
        }.runAsync(onLoad)
    }

    override fun loadRecommendedProducts(
        size: Int,
        onLoad: (Result<List<Product>>) -> Unit,
    ) {
        {
            val latestViewedProductId: Long =
                recentViewedProductsDataSource.load().maxByOrNull { it.viewedAt }?.productId
                    ?: error(NO_RECENT_VIEWED_PRODUCT_ERROR_MESSAGE)

            val latestViewedProduct: ProductEntity =
                productsDataSource.getProductById(latestViewedProductId)
                    ?: error(NO_RECENT_VIEWED_PRODUCT_ERROR_MESSAGE)

            val sameCategoryProducts: List<ProductEntity> =
                productsDataSource.products(category = latestViewedProduct.category)
            val cart: List<CartItemEntity> = cartDataSource.cart()

            sameCategoryProducts
                .filterNot { product ->
                    product.id in cart.map { cartItem: CartItemEntity -> cartItem.productId }
                }.map { it.toDomain() }
                .take(size)
        }.runAsync(onLoad)
    }

    private inline fun <T> (() -> T).runAsync(crossinline onResult: (Result<T>) -> Unit) {
        thread {
            val result = runCatching(this)
            onResult(result)
        }
    }

    companion object {
        private const val NO_RECENT_VIEWED_PRODUCT_ERROR_MESSAGE = "가장 최근 본 상품을 조회할 수 없습니다."
    }
}
