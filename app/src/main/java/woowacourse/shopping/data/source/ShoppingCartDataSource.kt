package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData

interface ShoppingCartDataSource {
    suspend fun findByProductId(productId: Long): Result<ProductIdsCountData>

    suspend fun findCartItemByProductId(productId: Long): Result<CartItemData>

    suspend fun loadAllCartItems(): Result<List<CartItemData>>

    suspend fun addNewProduct(productIdsCountData: ProductIdsCountData): Result<Unit>

    suspend fun removeCartItem(cartItemId: Long): Result<Unit>

    suspend fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit>
}
