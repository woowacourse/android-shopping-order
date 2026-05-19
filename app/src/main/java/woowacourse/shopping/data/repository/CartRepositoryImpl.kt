package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.remote.dto.response.cart.CartDto
import woowacourse.shopping.data.remote.dto.response.cart.CartProductDto
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class CartRepositoryImpl(
    private val api: CartApi,
) : CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun refreshCartItems() {
        _cartItems.value = getAllRemoteCartItems()
    }

    override suspend fun getCartItemQuantity(productId: String): Int? = getCartItem(productId)?.quantity

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) {
        val cartItem = getCartItem(productId)

        if (cartItem == null) {
            addCartItem(productId, quantity)
            refreshCartItems()
            return
        }

        updateQuantity(cartItem.id, quantity)
        refreshCartItems()
    }

    override suspend fun deleteItem(cartItemId: String) {
        api.deleteCartItem(id = cartItemId.toLong())
        refreshCartItems()
    }
    private suspend fun getAllRemoteCartItems(): List<CartItem> {

        val response =
            api.getCartItems(
                page = 0,
                size = Int.MAX_VALUE,
            )

        return response.content.map { it.toDomain() }
    }

    private fun getCartItem(productId: String): CartItem? = cartItems.value.firstOrNull { it.product.id == productId }

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

    private fun CartDto.toDomain(): CartItem =
        CartItem(
            id = id.toString(),
            product = product.toDomain(),
            quantity = quantity,
        )

    private fun CartProductDto.toDomain(): Product =
        Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl,
            category = category,
        )
}
