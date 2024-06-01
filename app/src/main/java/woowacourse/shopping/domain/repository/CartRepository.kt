package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.DataCallback
import woowacourse.shopping.domain.model.Quantity

interface CartRepository {
    fun findByProductId(
        productId: Int,
        totalItemCount: Int,
        dataCallback: DataCallback<CartItem?>,
    )

    fun syncFindByProductId(
        productId: Int,
        totalItemCount: Int,
    ): CartItem?

    fun findAll(dataCallback: DataCallback<List<CartItem>>)

    fun delete(
        id: Int,
        dataCallback: DataCallback<Unit>,
    )

    fun add(
        productId: Int,
        quantity: Quantity = Quantity(1),
        dataCallback: DataCallback<Unit>,
    )

    fun changeQuantity(
        id: Int,
        quantity: Quantity,
        dataCallback: DataCallback<Unit>,
    )

    fun getTotalQuantity(dataCallback: DataCallback<Int>)

    fun syncGetTotalQuantity(): Int
}
