package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.CartProduct

interface CartRemoteDataSource {
    fun getAll(callback: (List<CartProduct>?) -> Unit)
    fun postItem(itemId: Int, callback: (Int?) -> Unit)
    fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Int?) -> Unit)
    fun deleteItem(itemId: Int, callback: (Int?) -> Unit)
}
