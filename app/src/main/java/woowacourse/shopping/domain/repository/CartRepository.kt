package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface CartRepository {
    fun save(
        product: Product,
        quantity: Int = DEFAULT_QUANTITY,
    )

    fun update(
        productId: Int,
        quantity: Int,
    )

    fun cartItemSize(): Int

    fun productQuantity(productId: Int): Int

    fun findOrNullByProductId(productId: Int): CartItem?

    fun find(cartItemId: Int): CartItem

    fun findAll(): List<CartItem>

    fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): List<CartItem>

    fun delete(cartItemId: Int)

    fun deleteAll()

    companion object {
        const val DEFAULT_QUANTITY = 1
    }
}

interface CartRepository2 {
    fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    fun deleteCartItem(cartItemId: Int): Result<Unit>

    fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit>

    fun getCartTotalQuantity(): Result<CartQuantity>
}
