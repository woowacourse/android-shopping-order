package woowacourse.shopping.data.shoppingCart.repository

import okio.IOException
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequest
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequest
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

class DefaultShoppingCartRepository(
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRepository {
    override suspend fun load(
        page: Int,
        size: Int,
    ): Result<ShoppingCarts> {
        return runCatching {
            val result =
                shoppingCartService
                    .getCartItems(page, size)
            ShoppingCarts(
                last = result.last,
                shoppingCartItems = result.shoppingCartItems.map { it.toDomain() },
            )
        }
    }

    override suspend fun add(
        product: Product,
        quantity: Int,
    ): Result<ShoppingCartProduct> {
        return runCatching {
            shoppingCartService
                .postCartItem(
                    CartItemRequest(
                        productId = product.id,
                        quantity = quantity,
                    ),
                )

            load().getOrThrow().shoppingCartItems
                .firstOrNull { it.product.id == product.id } ?: throw IOException(
                ERR_NOT_ADDED_PRODUCT,
            )
        }
    }

    override suspend fun updateQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ): Result<ShoppingCartProduct?> {
        return runCatching {
            val requestDto = CartItemQuantityRequest(quantity = quantity)
            shoppingCartService
                .updateCartItemQuantity(
                    shoppingCartId = shoppingCartId,
                    cartItemQuantityRequest = requestDto,
                )
            load().getOrThrow().shoppingCartItems
                .firstOrNull { it.id == shoppingCartId }
        }
    }

    override suspend fun remove(shoppingCartId: Long): Result<Unit> {
        return runCatching {
            shoppingCartService.deleteCartItem(shoppingCartId)
        }
    }

    override suspend fun fetchAllQuantity(): Result<Int> {
        return runCatching {
            shoppingCartService.getCartCounts().quantity
        }
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ShoppingCartRepository? = null
        private const val ERR_NOT_ADDED_PRODUCT = "상품이 추가되지 않았습니다"

        fun initialize(shoppingCartService: ShoppingCartService) {
            INSTANCE =
                DefaultShoppingCartRepository(
                    shoppingCartService = shoppingCartService,
                )
        }

        fun get(): ShoppingCartRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
