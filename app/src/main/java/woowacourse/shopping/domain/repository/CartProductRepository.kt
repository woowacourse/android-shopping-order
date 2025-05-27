package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct

interface CartProductRepository {
    fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    )

    fun getQuantityByProductId(
        productId: Int,
        onSuccess: (Int?) -> Unit,
    )

    fun getTotalQuantity(onSuccess: (Int) -> Unit)

    fun updateQuantity(
        productId: Int,
        currentQuantity: Int,
        newQuantity: Int,
        onSuccess: () -> Unit,
    )

    fun deleteByProductId(
        productId: Int,
        onSuccess: () -> Unit,
    )
}
