package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.fixtures.fakeCartProduct

class FakeCartRepository(
    private var cart: Cart = Cart(),
) : CartRepository {
    private val products: List<CartProduct> get() = cart.cartProducts

    override suspend fun findCartProduct(productId: Long): Result<CartProduct> {
        val cartProduct =
            cart.findCartProductByProductId(productId) ?: return Result.failure(
                NoSuchElementException("Invalid product id"),
            )
        return Result.success(cartProduct)
    }

    override suspend fun loadCurrentPageCart(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart> {
        if (canLoadMoreCartProducts(currentPage, pageSize).getOrThrow()
                .not()
        ) {
            return Result.success(Cart())
        }
        val startIndex = (currentPage) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(products.size)

        if (startIndex >= products.size) {
            return Result.failure(
                IllegalArgumentException("Invalid page"),
            )
        }
        return Result.success(Cart(products.subList(startIndex, endIndex)))
    }

    override suspend fun loadCart(): Result<Cart> {
        return Result.success(cart)
    }

    override suspend fun filterCartProducts(productIds: List<Long>): Result<Cart> {
        return Result.success(cart.filterByProductIds(productIds))
    }

    override suspend fun createCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart> {
        cart =
            cart.add(
                fakeCartProduct(productId = product.id, name = "오둥이 $product.id", count = count),
            )
        return Result.success(cart)
    }

    override suspend fun updateCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart> {
        cart =
            cart.add(
                fakeCartProduct(productId = product.id, name = "오둥이 $product.id", count = count),
            )
        return Result.success(cart)
    }

    override suspend fun deleteCartProduct(productId: Long): Result<Cart> {
        cart = cart.delete(productId)
        return Result.success(cart)
    }

    override fun existsCartProduct(productId: Long): Boolean {
        return cart.hasProductId(productId)
    }

    override fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean> {
        if (currentPage < 0) return Result.success(false)
        val startIndex = currentPage * pageSize
        return Result.success(startIndex < products.size)
    }

    override fun clearCart() {
        TODO("Not yet implemented")
    }
}
