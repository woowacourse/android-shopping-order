package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.CartProductDTO
import woowacourse.shopping.data.remote.result.DataResult

interface CartDataSource {
    fun getAll(callback: (DataResult<List<CartProductDTO>>) -> Unit)
    fun insert(productId: Int, quantity: Int, callback: (DataResult<Int>) -> Unit)
    fun update(cartId: Int, quantity: Int, callback: (DataResult<Boolean>) -> Unit)
    fun remove(cartId: Int, callback: (DataResult<Boolean>) -> Unit)
}
