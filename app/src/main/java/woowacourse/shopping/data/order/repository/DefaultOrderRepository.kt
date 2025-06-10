package woowacourse.shopping.data.order.repository

import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

class DefaultOrderRepository(
    private val shoppingCartService: ShoppingCartService,
) : OrderRepository {
    override suspend fun removeProductsFromShoppingCart(shoppingCartItem: List<ShoppingCartProduct>): Result<Unit> {
        return runCatching {
            shoppingCartItem.map {
                shoppingCartService.deleteCartItem(it.id)
            }
        }
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: OrderRepository? = null
        private const val ERR_NOT_ADDED_PRODUCT = "상품이 추가되지 않았습니다"

        fun initialize(shoppingCartService: ShoppingCartService) {
            INSTANCE =
                DefaultOrderRepository(
                    shoppingCartService = shoppingCartService,
                )
        }

        fun get(): OrderRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
