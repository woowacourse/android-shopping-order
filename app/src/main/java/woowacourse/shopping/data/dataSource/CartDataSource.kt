package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.CartProductDto

interface CartDataSource {
    fun getAll(callback: (List<CartProductDto>?) -> Unit)
    fun postItem(itemId: Int, callback: (Int?) -> Unit)
    fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Int?) -> Unit)
    fun deleteItem(itemId: Int, callback: (Int?) -> Unit)
}
