package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.model.response.CartItemResponse

interface ShoppingCartDataSource {
    fun findByProductId(productId: Long): ProductIdsCountData?

    fun loadAllCartItems(): List<CartItemResponse>

    fun addNewProduct(productIdsCount: ProductIdsCount)

    fun removeCartItem(cartItemId: Long)

    fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    )

    fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    )

    fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    )
}
