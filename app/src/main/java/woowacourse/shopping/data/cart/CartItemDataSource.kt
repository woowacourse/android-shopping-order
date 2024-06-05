package woowacourse.shopping.data.cart

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemDto
import woowacourse.shopping.remote.cart.CartItemResponse

interface CartItemDataSource {
    fun loadAllCartItems(): ResponseResult<CartItemResponse>

    fun addedNewProductsId(productIdsCount: ProductIdsCount): ResponseResult<Unit>

    fun removedProductsId(cartItemId: Long): ResponseResult<Unit>

    fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>

    fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ): ResponseResult<Unit>
}
