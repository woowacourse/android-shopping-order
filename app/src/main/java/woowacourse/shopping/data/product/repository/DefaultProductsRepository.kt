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

class DefaultProductsRepository(
    private val productsDataSource: ProductsDataSource = RemoteProductsDataSource(),
    private val recentViewedProductsDataSource: RecentViewedProductsDataSource = LocalRecentViewedProductsDataSource,
    private val cartDataSource: CartDataSource = RemoteCartDataSource(),
) : ProductsRepository {
    override suspend fun loadPageableProducts(
        page: Int,
        size: Int,
    ): Result<Pageable<Product>> {
        val pageableProductData: PageableProductData =
            productsDataSource.pageableProducts(page = page, size = size)

        return runCatching {
            Pageable<Product>(
                items = pageableProductData.products.map { it.toDomain() },
                hasPrevious = false,
                hasNext = pageableProductData.loadable,
            )
        }
    }

    override suspend fun getProductById(id: Long): Result<Product?> =
        runCatching {
            val productEntity: ProductEntity? = productsDataSource.getProductById(id)
            productEntity?.toDomain()
        }

    override suspend fun loadLatestViewedProduct(): Result<Product?> =
        runCatching {
            val productId: Long? =
                recentViewedProductsDataSource.load().maxByOrNull { it.viewedAt }?.productId
            if (productId == null) {
                null
            } else {
                productsDataSource.getProductById(productId)?.toDomain()
            }
        }

    override suspend fun loadRecentViewedProducts(): Result<List<Product>> =
        runCatching {
            recentViewedProductsDataSource
                .load()
                .sortedByDescending { it.viewedAt }
                .mapNotNull { recentViewedProductEntity: RecentViewedProductEntity ->
                    productsDataSource
                        .getProductById(recentViewedProductEntity.productId)
                        ?.toDomain()
                }
        }

    override suspend fun addViewedProduct(product: Product): Result<Unit> =
        runCatching {
            recentViewedProductsDataSource.upsert(
                RecentViewedProductEntity(
                    productId = product.id,
                    viewedAt = LocalDateTime.now(),
                ),
            )
        }

    override suspend fun loadRecommendedProducts(size: Int): Result<List<Product>> =
        runCatching {
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
        }

    companion object {
        private const val NO_RECENT_VIEWED_PRODUCT_ERROR_MESSAGE = "가장 최근 본 상품을 조회할 수 없습니다."
    }
}
