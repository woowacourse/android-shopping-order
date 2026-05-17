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

class RetrofitCartRepository(
    private val encoder: BasicAuthEncoder,
    private val service: CartService,
) : CartRepository {
    override suspend fun getAllCartItems(): Cart {
        val response = service.getCartItems(
            auth = encoder.getHeader(),
        )
        return response.toDomain()
    }

    override suspend fun add(productId: Long, quantity: Int): Long {
        val response = service.addCartItem(
            auth = encoder.getHeader(),
            request = CartItemRequest(productId, quantity)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException(
                "addCartItem 실패: ${response.code()} ${response.errorBody()?.string()}"
            )
        }

        val location = response.headers()["Location"]
            ?: error("Location 헤더가 없습니다 (status=${response.code()})")
        return location.substringAfterLast("/").toLong()
    }

    override suspend fun updateQuantity(cartItemId: Long, quantity: Int) {
        service.updateCartItemQuantity(
            auth = encoder.getHeader(),
            cartItemId = cartItemId,
            quantity = Quantity(quantity)
        )
    }

    override suspend fun delete(cartItemId: Long) {
        service.deleteCartItem(
            auth = encoder.getHeader(),
            cartItemId = cartItemId
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

    override suspend fun getTotalProductCount(): Int =
        service.getTotalCount(auth = encoder.getHeader()).quantity
}
