package woowacourse.shopping.data.source.cart

import woowacourse.shopping.data.network.cart.RetrofitCartService
import woowacourse.shopping.data.network.cart.dto.CartItemInsertDto
import woowacourse.shopping.data.network.cart.dto.Quantity
import woowacourse.shopping.domain.CartContent

class CartServerDataSourceImpl(
    val retrofitCartService: RetrofitCartService,
) : CartServerDataSource {
    override suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent> {
        val body =
            retrofitCartService
                .requestCartItems(page = startIndex, size = pageSize)

        return body.content.map { it.toDomain() }
    }

    override suspend fun getTotalQuantity(): Int {
        val body =
            retrofitCartService
                .getCartItemTotalCount()

        return body.quantity
    }

    override suspend fun insertCartItem(item: CartContent) {
        retrofitCartService
            .insertCartItem(
                cartItemInsertDto =
                    CartItemInsertDto(
                        productId = item.product.id,
                        quantity = item.quantity,
                    ),
            )
    }

    override suspend fun updateCartItem(item: CartContent) {
        retrofitCartService
            .updateCartItemQuantity(
                id = item.id,
                quantity = Quantity(item.quantity),
            )
    }

    override suspend fun deleteById(id: Long) {
        retrofitCartService
            .deleteCartItem(id = id)
    }
}
