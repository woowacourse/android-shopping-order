package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.remote.dto.CartItemRequest
import woowacourse.shopping.data.remote.dto.Quantity
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.Page
import woowacourse.shopping.model.cart.Cart
import woowacourse.shopping.model.cart.CartItem

class RetrofitCartRepository(
    private val service: CartService,
) : CartRepository {
    override suspend fun getAllCartItems(): Cart {
        val response = service.getCartItems(size = 1)
        val totalSize = response.totalPages
        return service.getCartItems(0, totalSize).toDomain()
    }

    override suspend fun add(
        productId: Long,
        quantity: Int,
    ): Long {
        val response =
            service.addCartItem(
                request = CartItemRequest(productId, quantity),
            )
        if (!response.isSuccessful) {
            throw IllegalStateException(
                "addCartItem 실패: ${response.code()} ${response.errorBody()?.string()}",
            )
        }

        val location =
            response.headers()["Location"]
                ?: error("Location 헤더가 없습니다 (status=${response.code()})")
        return location.substringAfterLast("/").toLong()
    }

    override suspend fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        service.updateCartItemQuantity(
            cartItemId = cartItemId,
            quantity = Quantity(quantity),
        )
    }

    override suspend fun delete(cartItemId: Long) {
        service.deleteCartItem(
            cartItemId = cartItemId,
        )
    }

    override suspend fun getPagedItems(
        page: Int,
        size: Int,
    ): Page<CartItem> {
        val response =
            service.getCartItems(
                pageIndex = page,
                size = size,
            )

        return Page(
            items =
                response.content.map {
                    CartItem(
                        id = it.id,
                        product = it.product.toDomain(),
                        quantity = it.quantity,
                    )
                },
            isLast = response.last,
            totalPages = response.totalPages,
            currentPage = response.number,
            totalElements = response.totalElements.toInt(),
        )
    }

    override suspend fun getTotalProductCount(): Int = service.getTotalCount().quantity
}
