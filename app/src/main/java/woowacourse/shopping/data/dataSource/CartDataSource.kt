package woowacourse.shopping.data.dataSource

import woowacourse.shopping.model.CartProduct

interface CartDataSource {
    fun getAll(callback: (List<CartProduct>?) -> Unit)
    fun postItem(itemId: Int, callback: (Int?) -> Unit)
    fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Int?) -> Unit)
    fun deleteItem(itemId: Int, callback: (Int?) -> Unit)
}
