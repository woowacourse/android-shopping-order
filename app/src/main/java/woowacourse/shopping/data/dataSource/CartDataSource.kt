package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.CartProductDto

interface CartDataSource {
    fun getAll(
        onSuccess: (List<CartProductDto>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun postItem(
        itemId: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun patchItemQuantity(
        itemId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun deleteItem(
        itemId: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )
}
