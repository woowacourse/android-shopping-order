package woowacourse.shopping.data.repository

import woowacourse.shopping.data.localdb.mapper.toDomain
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money

class CartRepositoryImpl(
    private val api: CartApi,
) : CartRepository {
    override suspend fun getTotalPrice(cartIds: List<String>): Money =
        getAllCartItems()
            .filter { cartIds.contains(it.id) }
            .fold(Money(0)) { acc, cartItem ->
                acc + cartItem.product.price * cartItem.quantity
            }

    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult {
        val apiResult =
            api.getCartItems(
                page = page,
                size = size,
            )

        val cartItems = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return CartResponseResult(cartItems, lastPage)
    }

    override suspend fun getCartItemQuantity(productId: String): Int? = getCartItem(productId)?.quantity

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) {
        val cartItem = getCartItem(productId)

        if (cartItem == null) {
            addCartItem(productId, quantity)

            return
        }

        updateQuantity(cartItem.id, quantity)
    }

    override suspend fun deleteItem(cartItemId: String) {
        api.deleteCartItem(id = cartItemId.toLong())
    }

    override suspend fun getTotalCartItemQuantity(): Int = api.getCartItemsQuantity().quantity

    override suspend fun getCartItemsCount(): Int = getAllCartItems().size

    private suspend fun addCartItem(
        productId: String,
        quantity: Int,
    ) {
        api.addCartItem(
            AddCartRequestBody(
                productId = productId.toLong(),
                quantity = quantity,
            ),
        )
    }

    private suspend fun updateQuantity(
        cartItemId: String,
        quantity: Int,
    ) {
        api.updateCartItem(
            id = cartItemId.toLong(),
            updateCartRequestBody = UpdateCartRequestBody(quantity = quantity),
        )
    }

    private suspend fun getAllCartItems(): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()

        val response =
            api.getCartItems(
                page = 0,
                size = Int.MAX_VALUE,
            )

        cartItems += response.content.map { it.toDomain() }

        return cartItems
    }

    private suspend fun getCartItem(productId: String): CartItem? {
        val cartItems = getAllCartItems()

        return cartItems.firstOrNull { it.product.id == productId }
    }
}

data class CartResponseResult(
    val cartItems: List<CartItem>,
    val isLastPage: Boolean,
)
