package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.datasource.RemoteProductDataSource
import woowacourse.shopping.data.local.database.RecentProductDao
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.cart.toCartData
import woowacourse.shopping.data.model.product.toOrderableProduct
import woowacourse.shopping.data.model.product.toProductDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.math.max

class ProductRepositoryImpl(
    private val remoteProductDataSource: RemoteProductDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
    private val recentProductDao: RecentProductDao,
) : ProductRepository {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductDomain> {
        return runCatching {
            remoteProductDataSource.getProducts(category, page, size, sort)
                .toProductDomain(getEntireCartItems())
        }
    }

    override suspend fun getProductById(id: Int): Result<OrderableProduct> {
        return runCatching {
            val cartItem =
                getEntireCartItems().firstOrNull {
                    it.productId == id
                }
            remoteProductDataSource
                .getProductById(id)
                .toOrderableProduct(cartItem)
        }
    }

    override suspend fun getRecommendedProducts(requiredSize: Int): Result<List<OrderableProduct>> {
        return runCatching {
            val lastlyViewedProduct = recentProductDao.findMostRecentProduct()
            val cartData = getEntireCartItems()
            remoteProductDataSource.getRecommendedProducts(
                category = lastlyViewedProduct?.category,
                maxSize = requiredSize,
                sort = SORT_RECOMMENDED_ITEMS
            ).toProductDomain(cartData = cartData)
                .orderableProducts
                .filter { it.cartData == null }
        }
    }

    private suspend fun getEntireCartItems(): List<CartData> {
        val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
        return remoteCartDataSource.getCartItems(
            PAGE_CART_ITEMS,
            totalCartQuantity,
            SORT_CART_ITEMS,
        )
            .cartItems
            .map(CartItem::toCartData)
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val SORT_CART_ITEMS = "asc"
        private const val SORT_RECOMMENDED_ITEMS = "asc"
    }
}
