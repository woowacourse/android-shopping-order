package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData

interface ShoppingCartDataSource {
    suspend fun findByProductId2(productId: Long): Result<ProductIdsCountData>

    suspend fun loadAllCartItems2(): Result<List<CartItemData>>

    suspend fun addNewProduct2(productIdsCountData: ProductIdsCountData): Result<Unit>

    suspend fun removeCartItem2(cartItemId: Long): Result<Unit>

    suspend fun plusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun minusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateProductsCount2(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit>
}
