package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct

interface CartProductRepository {
    fun insert(
        productId: Int,
        quantity: Int,
        onSuccess: (Int) -> Unit,
    )

    fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    )

    fun getCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct?) -> Unit,
    )

    fun getTotalQuantity(onSuccess: (Int) -> Unit)

    fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
        onSuccess: () -> Unit,
    )

    fun delete(
        id: Int,
        onSuccess: () -> Unit,
    )
}
