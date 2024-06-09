package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.datasource.RemoteProductDataSource
import woowacourse.shopping.data.local.database.RecentProductDao
import woowacourse.shopping.data.model.cart.CartItem
import woowacourse.shopping.data.model.cart.toCartData
import woowacourse.shopping.data.model.product.toOrderableProduct
import woowacourse.shopping.data.model.product.toProductDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.getFixtureOrderableProducts
import kotlin.math.min

class FakeProductRepository : ProductRepository {
    private val recentProductRepository = FakeRecentProductRepository()
    private val cartRepository = FakeCartRepository()
    private var orderableProducts = getFixtureOrderableProducts(130)

    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<ProductDomain> {
        return runCatching {
            val fromIndex = page * 20
            val toIndex = min(fromIndex + 20, orderableProducts.size)
            val last = orderableProducts.getOrNull(toIndex) == null
            ProductDomain(
                orderableProducts = orderableProducts.subList(fromIndex, toIndex),
                last = last
            )

        }
    }

    override suspend fun getProductById(id: Int): Result<OrderableProduct> {
        return runCatching {
            orderableProducts.first { it.productItemDomain.id == id }
        }
    }

    override suspend fun getRecommendedProducts(): Result<List<OrderableProduct>> {
        return runCatching {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()
            val cartData = getEntireCartItems()
            val orderableProducts = mutableListOf<OrderableProduct>()
            var page = 0
            do {
                val products =
                    getProducts(
                        category = lastlyViewedProduct?.category,
                        page = page++,
                        size = RECOMMEND_PAGE_SIZE,
                        sort = SORT_CART_ITEMS,
                    ).getOrThrow().orderableProducts.filter {
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
        val totalCartQuantity = cartRepository.getCartTotalQuantity().getOrThrow()
        return cartRepository.getCartItems(
            PAGE_CART_ITEMS,
            totalCartQuantity,
            SORT_CART_ITEMS,
        ).getOrThrow().cartItems.map {
            CartData(it.cartItemId, it.product.id, it.quantity)
        }
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val RECOMMEND_PAGE_SIZE = 10
        private const val SORT_CART_ITEMS = "asc"
    }
}
