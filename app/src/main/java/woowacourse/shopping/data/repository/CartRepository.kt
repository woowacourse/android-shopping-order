package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.domain.model.CartProduct

interface CartRepository {
    fun getAll(callback: (DataResult<List<CartProduct>>) -> Unit)
    fun insert(productId: Int, quantity: Int, callback: (DataResult<Int>) -> Unit)
    fun update(cartId: Int, quantity: Int, callback: (DataResult<Boolean>) -> Unit)
    fun remove(cartId: Int, callback: (DataResult<Boolean>) -> Unit)
}
