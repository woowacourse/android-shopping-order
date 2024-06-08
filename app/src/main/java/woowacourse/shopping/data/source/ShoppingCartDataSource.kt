package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData

interface ShoppingCartDataSource {
    fun findByProductId(productId: Long): ProductIdsCountData?

    fun loadAllCartItems(): List<CartItemData>

    fun addNewProduct(productIdsCountData: ProductIdsCountData)

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
