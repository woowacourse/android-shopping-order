package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

class DefaultShoppingCartRepository(
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRepository {
    override suspend fun load(
        page: Int,
        size: Int,
    ): ShoppingCarts {
        val result =
            shoppingCartService
                .getCartItems(page, size)
        return ShoppingCarts(
            last = result.last,
            shoppingCartItems = result.shoppingCartItems.map { it.toDomain() },
        )
    }

    override suspend fun add(
        product: Product,
        quantity: Int,
    ) {
        shoppingCartService
            .postCartItem(
                CartItemRequestDto(
                    productId = product.id,
                    quantity = quantity,
                ),
            )
    }

    override suspend fun updateQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ) {
        val requestDto = CartItemQuantityRequestDto(quantity = quantity)
        shoppingCartService
            .updateCartItemQuantity(
                shoppingCartId = shoppingCartId,
                cartItemQuantityRequestDto = requestDto,
            )
    }

    override suspend fun remove(shoppingCartId: Long) {
        shoppingCartService.deleteCartItem(shoppingCartId)
    }

    override suspend fun fetchAllQuantity(): Int = shoppingCartService.getCartCounts().quantity

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ShoppingCartRepository? = null

        fun initialize(shoppingCartService: ShoppingCartService) {
            if (INSTANCE == null) {
                INSTANCE =
                    DefaultShoppingCartRepository(
                        shoppingCartService = shoppingCartService,
                    )
            }
        }

        fun get(): ShoppingCartRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
