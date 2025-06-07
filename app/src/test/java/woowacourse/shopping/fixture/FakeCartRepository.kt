package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository(
    initialCartProducts: List<CartProduct> = emptyList(),
) : CartRepository {
    private val cartItems: MutableMap<Long, CartProduct> =
        initialCartProducts.associateBy { it.product.id }.toMutableMap()

    override suspend fun fetchCart(): Result<Unit> = Result.success(Unit)

    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>> {
        val sortedItems = cartItems.values.sortedBy { it.cartId }
        val offset = page * size
        val pagedItems = sortedItems.drop(offset).take(size)
        val hasMore = (offset + size) < sortedItems.size
        return Result.success(PageableItem(pagedItems, hasMore))
    }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> {
        if (cartItems.remove(cartId) != null) {
            return Result.success(Unit)
        }
        return Result.failure(NoSuchElementException(ERROR_CART_PRODUCT_NOT_FOUND.format(cartId)))
    }

    override suspend fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> {
        val cartProduct = cartItems[productId]
        val product = productsFixture.find { it.id == productId }?.toDomain()
        product ?: return Result.failure(NoSuchElementException("Product not found: $productId"))

        if (cartProduct == null) {
            cartItems[productId] = CartProduct(0, product, increaseCount)
            return Result.success(Unit)
        }

        cartItems[productId] = cartProduct.copy(quantity = cartProduct.quantity + increaseCount)
        return Result.success(Unit)
    }

    override suspend fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> {
        val existingItem = cartItems[productId]

        if (existingItem != null) {
            val newQuantity = existingItem.quantity - decreaseCount
            if (newQuantity <= 0) {
                cartItems.remove(productId)
                return Result.success(Unit)
            }

            cartItems[productId] = existingItem.copy(quantity = newQuantity)
            return Result.success(Unit)
        }

        return Result.failure(NoSuchElementException(ERROR_QUANTITY_NOT_FOUND.format(productId)))
    }

    override suspend fun fetchCartProductCount(): Result<Int> = Result.success(cartItems.size)

    override suspend fun findQuantityByProductId(productId: Long): Result<Int> {
        val cartProduct = cartItems[productId]
        return Result.success(cartProduct?.quantity ?: 0)
    }

    override suspend fun findCartProductByProductId(productId: Long): Result<CartProduct> {
        val cartProduct =
            cartItems[productId]
                ?: return Result.failure(
                    NoSuchElementException(
                        ERROR_CART_PRODUCT_NOT_FOUND.format(
                            productId,
                        ),
                    ),
                )
        return Result.success(cartProduct)
    }

    override suspend fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> {
        val result = productIds.mapNotNull { cartItems[it] }
        return Result.success(result)
    }

    override suspend fun getAllCartProducts(): Result<List<CartProduct>> = Result.success(cartItems.values.toList())

    companion object {
        private const val ERROR_CART_PRODUCT_NOT_FOUND = "해당 ID(%d)의 장바구니 상품이 존재하지 않습니다."
        private const val ERROR_QUANTITY_NOT_FOUND = "해당 상품 ID(%d)의 수량을 찾을 수 없습니다."
    }
}
