package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.remote.dto.response.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.response.cart.CartProductResponse
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class CartRepositoryImpl(
    private val api: CartApi,
) : CartRepository {
    override suspend fun getTotalPrice(cartIds: List<Long>): Money {
        val cartItems = getAllCartItems()

        return cartItems
            .filter { cartIds.contains(it.id) }
            .fold(Money(0)) { acc, cartItem ->
                acc + cartItem.product.price * cartItem.quantity
            }
    }

    private suspend fun getAllCartItems(): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()

        val response =
            api.getCartItems(
                page = 0,
                size = MAX_CART_ITEM_LIMIT,
            )

        cartItems += response.content.map { it.toDomain() }

        return cartItems
    }

    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult {
        val apiResult =
            api
                .getCartItems(
                    page = page,
                    size = size,
                )

        val cartItems = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return CartResponseResult(cartItems, lastPage)
    }

    private suspend fun getCartItem(productId: Long): CartItem? {
        val cartItems = getAllCartItems()

        return cartItems.firstOrNull { it.product.id == productId }
    }

    override suspend fun getCartItemQuantity(productId: Long): Int? = getCartItem(productId)?.quantity

    override suspend fun setCartItem(
        productId: Long,
        quantity: Int,
    ) {
        val cartItem = getCartItem(productId)

        if (cartItem == null) {
            addCartItem(productId, quantity)
            return
        }

        updateQuantity(cartItem.id, quantity)
    }

    override suspend fun deleteItem(cartItemId: Long) {
        api.deleteCartItem(id = cartItemId)
    }

    override suspend fun getTotalCartItemQuantity(): Int = api.getCartItemsQuantity().quantity

    override suspend fun getCartItemsCount(): Int = getAllCartItems().size

    private suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        api.addCartItem(
            AddCartRequestBody(
                productId = productId,
                quantity = quantity,
            ),
        )
    }

    private suspend fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        api.updateCartItem(
            id = cartItemId,
            updateCartRequestBody = UpdateCartRequestBody(quantity = quantity),
        )
    }

    private fun CartItemResponse.toDomain(): CartItem =
        CartItem(
            id = id,
            product = product.toDomain(),
            quantity = quantity,
        )

    private fun CartProductResponse.toDomain(): Product =
        Product(
            id = id,
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl,
            category = category,
        )

    companion object {
        private const val MAX_CART_ITEM_LIMIT = 100
    }
}

data class CartResponseResult(
    val cartItems: List<CartItem>,
    val isLastPage: Boolean,
)
