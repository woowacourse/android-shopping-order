package woowacourse.shopping.repository.network

import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.network.auth.BasicAuthEncoder
import woowacourse.shopping.network.dto.CartItemRequest
import woowacourse.shopping.network.dto.Quantity
import woowacourse.shopping.network.dto.toDomain
import woowacourse.shopping.network.service.CartService
import woowacourse.shopping.repository.CartRepository

class RetrofitCartRepository(
    private val encoder: BasicAuthEncoder,
    private val service: CartService
) : CartRepository {
    override suspend fun getAllCartItems(): Cart {
        val response = service.getCartItems(
            auth = encoder.getHeader(),
        )

        return response.toDomain()
    }

    override suspend fun add(item: Product, quantity: Int) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) {
            service.addCartItem(
                auth = encoder.getHeader(),
                request = CartItemRequest(
                    productId = item.id, quantity = quantity
                )
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId = foundedItem.id
                    ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
                quantity = Quantity(foundedItem.quantity + quantity),
            )
        }
    }

    override suspend fun increase(item: Product) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) {
            service.addCartItem(
                auth = encoder.getHeader(),
                request = CartItemRequest(
                    productId = item.id, quantity = 1
                )
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId = foundedItem.id
                    ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
                quantity = Quantity(foundedItem.quantity + 1),
            )
        }
    }

    override suspend fun decrease(item: Product) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) return
        if (foundedItem.quantity == 1) {
            service.deleteCartItem(
                auth = encoder.getHeader(),
                cartItemId = foundedItem.id
                    ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다.")
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId = foundedItem.id
                    ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
                quantity = Quantity(foundedItem.quantity - 1)
            )
        }
    }

    override suspend fun delete(item: Product) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) return
        service.deleteCartItem(
            auth = encoder.getHeader(),
            cartItemId = foundedItem.id
                ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
        )
    }

    override suspend fun getPagedItems(
        fromIndex: Int,
        count: Int
    ): List<CartItem> {
        val response = service.getCartItems(
            encoder.getHeader(),
            page = fromIndex,
            size = count
        )

        return response.content.map {
            CartItem(
                id = it.id,
                product = it.product.toDomain(),
                quantity = it.quantity,
            )
        }
    }

    override suspend fun getSize(): Int = getAllCartItems().items.size

    override suspend fun getCartCount(): Int =
        service.getTotalCount(auth = encoder.getHeader()).quantity


}
