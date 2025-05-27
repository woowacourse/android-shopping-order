package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods

interface CartRepository {

    fun fetchAllCartItems(onComplete: (List<CartItem>) -> Unit, onFail: (Throwable) -> Unit)

    fun fetchPageCartItems(
        limit: Int,
        offset: Int,
        onComplete: (List<CartItem>) -> Unit,
        onFail: (Throwable) -> Unit
    )

    fun addOrIncreaseQuantity(
        goods: Goods,
        addQuantity: Int,
        onComplete: () -> Unit,
    )

    fun removeOrDecreaseQuantity(
        goods: Goods,
        removeQuantity: Int,
        onComplete: () -> Unit,
    )

    fun delete(
        goods: Goods,
        onComplete: () -> Unit,
    )

    fun getAllItemsSize(onComplete: (Int) -> Unit)
}
