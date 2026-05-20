package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

interface CartRepository {
    suspend fun insert(purchaseProduct: PurchaseProduct): ApiResult<Unit>

    suspend fun deleteCartItem(purchaseProductId: Long): ApiResult<Unit>

    suspend fun updateCount(
        cartItemId: Long,
        newQuantity: Int,
    ): ApiResult<Unit>

    suspend fun getProductCount(): ApiResult<Int>

    suspend fun getPagedCart(
        page: Int,
        size: Int,
    ): ApiResult<PurchaseProducts>

    suspend fun getCartItemCount(): ApiResult<Int>
}
