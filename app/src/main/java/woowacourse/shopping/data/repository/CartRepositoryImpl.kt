package woowacourse.shopping.data.repository

import android.util.Log
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

    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult {
        val apiResult = api
            .getCartItems(
                page = page,
                size = size,
            )

        val cartItems = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return CartResponseResult(cartItems, lastPage)
    }

    private suspend fun getCartItem(productId: String): CartItem? {
        val cartItems = getAllCartItems()

        return cartItems.firstOrNull { it.product.id == productId }
    }

    override suspend fun getCartItemQuantity(cartItemId: String): Int? =
        getCartItem(cartItemId)?.quantity

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) {
        val cartItem = getCartItem(productId)
        Log.d("okhttp1", "$cartItem")

        if (cartItem == null) {
            addCartItem(productId, quantity)

            Log.d("okhttp1", "addCartItem")
            return
        }

        updateQuantity(cartItem.id, quantity)
    }

    override suspend fun deleteItem(cartItemId: String) {
        api.deleteCartItem(id = cartItemId.toLong())
    }

    override suspend fun getTotalCartItemQuantity(): Int = api.getCartItemsQuantity().quantity

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
        )
}

data class CartResponseResult(
    val cartItems: List<CartItem>,
    val isLastPage: Boolean,
)
