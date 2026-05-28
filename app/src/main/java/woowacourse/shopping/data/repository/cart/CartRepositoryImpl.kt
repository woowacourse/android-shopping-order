package woowacourse.shopping.data.repository.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _selectedCartItemIds = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    override val selectedCartItemIds: StateFlow<ImmutableList<String>> = _selectedCartItemIds.asStateFlow()

    override suspend fun refreshCartItems() {
        _cartItems.value = getAllRemoteCartItems()
        syncSelectedCartItems()
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

    override suspend fun deleteSelectedItems() {
        val selectedCartItemIds = selectedCartItemIds.value

        try {
            selectedCartItemIds.forEach { cartItemId ->
                api.deleteCartItem(id = cartItemId.toLong())
            }
        } finally {
            refreshCartItems()
        }
    }

    override fun toggleCartItemSelection(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                (selectedItemsId - cartItemId).toImmutableList()
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    override fun selectCartItem(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                selectedItemsId
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    override fun unselectCartItem(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            (selectedItemsId - cartItemId).toImmutableList()
        }
    }

    override fun selectAllCartItems() {
        _selectedCartItemIds.value = cartItems.value.map { it.id }.toImmutableList()
    }

    override fun clearCartItemSelection() {
        _selectedCartItemIds.value = persistentListOf()
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

    private fun syncSelectedCartItems() {
        val cartItemIds = cartItems.value.map { it.id }.toSet()

        _selectedCartItemIds.update { selectedItemsId ->
            selectedItemsId.filter { it in cartItemIds }.toImmutableList()
        }
    }

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
