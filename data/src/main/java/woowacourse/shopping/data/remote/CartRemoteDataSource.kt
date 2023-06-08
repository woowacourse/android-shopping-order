package woowacourse.shopping.data.remote

import woowacourse.shopping.model.CartProduct

interface CartRemoteDataSource {
    fun getAll(): Result<List<CartProduct>>
    fun postItem(itemId: Int): Result<Int>
    fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int>
    fun deleteItem(itemId: Int): Result<Int>
}
