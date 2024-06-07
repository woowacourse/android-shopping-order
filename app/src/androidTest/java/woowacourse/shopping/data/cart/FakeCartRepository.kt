package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.fixtures.fakeProduct

class FakeCartRepository(
    private var cart: Cart = Cart(),
) : CartRepository {
    private val products: List<CartProduct> get() = cart.cartProducts()

    override suspend fun cartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> {
        if (canLoadMoreCartProducts(currentPage, pageSize).getOrThrow().not()) {
            return Result.success(emptyList())
        }
        val startIndex = (currentPage) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(products.size)

        if (startIndex >= products.size) {
            return Result.failure(IllegalArgumentException("Invalid page"))
        }
        return Result.success(products.subList(startIndex, endIndex))
    }

    override fun totalCartProducts(): Result<List<CartProduct>> {
        return Result.success(products)
    }

    override suspend fun filterCartProducts(productIds: List<Long>): Result<List<CartProduct>> {
        return Result.success(products.filter { it.product.id in productIds })
    }

    override suspend fun updateCartProduct(
        productId: Long,
        count: Int,
    ): Result<Unit> {
        val preCount = cart.cartProducts().find { it.product.id == productId }?.count ?: 0
        if (preCount == count) error("Same count")
        if (preCount < count) {
            cart = cart.add(fakeProduct(id = productId, name = "오둥이 $productId"))
        } else {
            cart = cart.remove(fakeProduct(id = productId, name = "오둥이 $productId"))
        }
        return Result.success(Unit)
    }

    override suspend fun deleteCartProduct(productId: Long): Result<Unit> {
        cart = cart.remove(fakeProduct(id = productId, name = "오둥이 $productId"))
        return Result.success(Unit)
    }

    override fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean> {
        if (currentPage < 0) return Result.success(false)
        val startIndex = currentPage * pageSize
        return Result.success(startIndex < products.size)
    }

    override fun orderCartProducts(productIds: List<Long>): Result<Unit> {
        TODO("Not yet implemented")
    }
}
