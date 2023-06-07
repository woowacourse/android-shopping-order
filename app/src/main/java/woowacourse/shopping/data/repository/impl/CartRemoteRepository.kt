package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.domain.model.CartProduct

class CartRemoteRepository(
    private val cartDataSource: CartDataSource,
) : CartRepository {

    override fun getAll(callback: (DataResult<List<CartProduct>>) -> Unit) {
        cartDataSource.getAll {
            when (it) {
                is DataResult.Success -> callback(DataResult.Success(it.response.map { cartProduct -> cartProduct.toDomain() }))
                is DataResult.Failure -> callback(it)
                is DataResult.NotSuccessfulError -> callback(it)
                is DataResult.WrongResponse -> callback(it)
            }
        }
    }

    override fun insert(productId: Int, quantity: Int, callback: (DataResult<Int>) -> Unit) {
        cartDataSource.insert(productId, quantity, callback)
    }

    override fun update(cartId: Int, quantity: Int, callback: (DataResult<Boolean>) -> Unit) {
        cartDataSource.update(cartId, quantity, callback)
    }

    override fun remove(cartId: Int, callback: (DataResult<Boolean>) -> Unit) {
        cartDataSource.remove(cartId, callback)
    }
}
