package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.random.Random

class FakeCartRepository(
    initialCartProducts: List<CartProduct> = emptyList(),
    private val products: List<ProductResponse> = productsFixture,
) : CartRepository {
    private val cartItems: MutableMap<Long, CartProduct> =
        initialCartProducts.associateBy { it.product.id }.toMutableMap()

    override fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    ) {
        val sortedItems = cartItems.values.sortedBy { it.cartId }
        val offset = page * size
        val pagedItems = sortedItems.drop(offset).take(size)
        val hasMore = (offset + size) < sortedItems.size
        onResult(Result.success(PageableItem(pagedItems, hasMore)))
        println("fetch: $pagedItems")
    }

    override fun deleteCartItem(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val keyToRemove = cartItems.entries.find { it.value.cartId == cartId }?.key
        if (keyToRemove != null) {
            cartItems.remove(keyToRemove)
        }
        onResult(Result.success(Unit))
    }

    override fun findQuantityByProductId(productId: Long): Result<Int> {
        val quantity = cartItems[productId]?.quantity ?: 0
        return Result.success(quantity)
    }

    override fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val existingItem = cartItems[productId]
        if (existingItem != null) {
            cartItems[productId] =
                existingItem.copy(quantity = existingItem.quantity + increaseCount)
        } else {
            val product = products.find { it.id == productId }?.toDomain()
            if (product != null) {
                cartItems[productId] =
                    CartProduct(
                        cartId = Random.nextLong(),
                        product = product,
                        quantity = increaseCount,
                    )
            }
        }
        onResult(Result.success(Unit))
    }

    override fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val existingItem = cartItems[productId]
        if (existingItem != null) {
            val newQuantity = existingItem.quantity - decreaseCount
            if (newQuantity > 0) {
                cartItems[productId] = existingItem.copy(quantity = newQuantity)
            } else {
                cartItems.remove(productId)
            }
        }
        onResult(Result.success(Unit))
    }

    override fun fetchCartItemCount(onResult: (Result<Int>) -> Unit) {
        onResult(Result.success(cartItems.size))
    }

    override fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> {
        val foundCartProducts = cartItems.filterKeys { it in productIds }.values.toList()
        return Result.success(foundCartProducts)
    }
}
