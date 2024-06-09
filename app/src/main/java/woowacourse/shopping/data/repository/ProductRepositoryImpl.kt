package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.datasource.RemoteProductDataSource
import woowacourse.shopping.data.local.database.RecentProductDao
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.cart.toCartData
import woowacourse.shopping.data.model.product.toOrderableProduct
import woowacourse.shopping.data.model.product.toProductDomain2
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.repository.ProductRepository

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
                .toProductDomain2(getEntireCartItems())
        }
    }

    override suspend fun getProductById(id: Int): Result<OrderableProduct> {
        return runCatching {
            val cartItem = getEntireCartItems().firstOrNull { it.productId == id }
            remoteProductDataSource.getProductById(id).toOrderableProduct(cartItem)
        }
    }

    override suspend fun getRecommendedProducts(): Result<List<OrderableProduct>> {
        return runCatching {
            val lastlyViewedProduct = recentProductDao.findMostRecentProduct()
            val cartData = getEntireCartItems()
            val orderableProducts = mutableListOf<OrderableProduct>()
            var page = 0
            do {
                val products =
                    remoteProductDataSource.getProducts(
                        category = lastlyViewedProduct?.category,
                        page = page++,
                        size = RECOMMEND_PAGE_SIZE,
                        sort = SORT_CART_ITEMS,
                    ).toProductDomain2(cartData).orderableProducts.filter {
                        it.cartData == null
                    }
                products.forEach {
                    if (orderableProducts.size < 10) {
                        orderableProducts.add(it)
                    } else {
                        return@forEach
                    }
                }
            } while (orderableProducts.size >= 10 || products.isEmpty())
            orderableProducts
        }
    }

    private suspend fun getEntireCartItems(): List<CartData> {
        val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
        return remoteCartDataSource.getCartItems(
            PAGE_CART_ITEMS,
            totalCartQuantity,
            SORT_CART_ITEMS,
        ).cartItems.map(CartItem::toCartData)
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val RECOMMEND_PAGE_SIZE = 10
        private const val SORT_CART_ITEMS = "asc"
    }
}
