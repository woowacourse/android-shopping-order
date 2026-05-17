package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.remote.auth.BasicAuthEncoder
import woowacourse.shopping.data.remote.dto.CartItemRequest
import woowacourse.shopping.data.remote.dto.Quantity
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Page
import woowacourse.shopping.model.Product

class RetrofitCartRepository(
    private val encoder: BasicAuthEncoder,
    private val service: CartService,
) : CartRepository {
    override suspend fun getAllCartItems(): Cart {
        val response =
            service.getCartItems(
                auth = encoder.getHeader(),
            )

        return response.toDomain()
    }

    override suspend fun add(
        item: Product,
        quantity: Int,
    ) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) {
            service.addCartItem(
                auth = encoder.getHeader(),
                request =
                    CartItemRequest(
                        productId = item.id,
                        quantity = quantity,
                    ),
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId =
                    foundedItem.id
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
                request =
                    CartItemRequest(
                        productId = item.id,
                        quantity = 1,
                    ),
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId =
                    foundedItem.id
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
                cartItemId =
                    foundedItem.id
                        ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
            )
        } else {
            service.updateCartItemQuantity(
                auth = encoder.getHeader(),
                cartItemId =
                    foundedItem.id
                        ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
                quantity = Quantity(foundedItem.quantity - 1),
            )
        }
    }

    override suspend fun delete(item: Product) {
        val cartItems = getAllCartItems()
        val foundedItem = cartItems.items.find { it.product.id == item.id }

        if (foundedItem == null) return
        service.deleteCartItem(
            auth = encoder.getHeader(),
            cartItemId =
                foundedItem.id
                    ?: throw IllegalArgumentException("찾은 카트 상품에 id가 없습니다."),
        )
    }

    override suspend fun getPagedItems(
        page: Int,
        size: Int,
    ): Page<CartItem> {
        val response = service.getCartItems(
                encoder.getHeader(),
                pageIndex = page,
                size = size,
            )

        return Page(
            items = response.content.map {
                CartItem(
                    id = it.id,
                    product = it.product.toDomain(),
                    quantity = it.quantity,
                )
            },
            isLast = response.last,
            totalPages = response.totalPages,
            currentPage = response.number,
            totalElements = response.totalElements.toInt()
        )
    }

    override suspend fun getCartEntitySize(): Int = getAllCartItems().items.size

    override suspend fun getCartCount(): Int =
        service.getTotalCount(auth = encoder.getHeader()).quantity
}
