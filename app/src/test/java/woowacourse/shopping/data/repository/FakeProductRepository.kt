package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.getFixtureOrderableProducts
import kotlin.math.min

class FakeProductRepository(
    count: Int = 130,
    cartCount: Int = 10,
) : ProductRepository {
    private val recentProductRepository = FakeRecentProductRepository()
    private val cartRepository = FakeCartRepository(cartCount)
    private var orderableProducts = getFixtureOrderableProducts(count, cartCount)

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
                last = last,
            )
        }
    }

    override suspend fun getProductById(id: Int): Result<OrderableProduct> {
        return runCatching {
            orderableProducts.first { it.productItemDomain.id == id }
        }
    }

    override suspend fun getRecommendedProducts(requiredSize: Int): Result<List<OrderableProduct>> {
        return runCatching {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()
            val orderableProducts = mutableListOf<OrderableProduct>()
            var page = 0
            do {
                val products =
                    getProducts(
                        category = lastlyViewedProduct?.category,
                        page = page++,
                        size = requiredSize,
                        sort = SORT_CART_ITEMS,
                    ).getOrThrow().orderableProducts.filter {
                        it.cartData == null
                    }
                products.forEach {
                    if (orderableProducts.size < requiredSize) {
                        orderableProducts.add(it)
                    } else {
                        return@forEach
                    }
                }
            } while (orderableProducts.size >= requiredSize || products.isEmpty())
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
        private const val SORT_CART_ITEMS = "asc"
    }
}
