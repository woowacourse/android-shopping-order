package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.CartProduct

interface CartRemoteDataSource {
    fun getAll(callback: (Result<List<CartProduct>>) -> Unit)
    fun postItem(itemId: Int, callback: (Result<Int>) -> Unit)
    fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Result<Int>) -> Unit)
    fun deleteItem(itemId: Int, callback: (Result<Int>) -> Unit)
}
