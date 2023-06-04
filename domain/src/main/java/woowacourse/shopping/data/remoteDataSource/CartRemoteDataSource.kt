package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.dto.CartProductDto

interface CartRemoteDataSource {
    fun getAll(): Result<List<CartProductDto>>
    fun postItem(itemId: Int): Result<Int>
    fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int>
    fun deleteItem(itemId: Int): Result<Int>
}
