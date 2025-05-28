package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    fun getTotalCount(onResult: (Result<Int>) -> Unit)

    fun fetchPagedCartItems(
        page: Int,
        pageSize: Int,
        onResult: (Result<List<CartItem>>) -> Unit,
    )
//
//    fun getTotalQuantity(onResult: (Result<Int?>) -> Unit)
//
//    fun insertProduct(
//        cartItem: CartItem,
//        onResult: (Result<Unit>) -> Unit,
//    )
//
//    fun insertOrIncrease(
//        productId: Long,
//        quantity: Int,
//        onResult: (Result<Unit>) -> Unit,
//    )
//
//    fun increaseQuantity(
//        productId: Long,
//        quantity: Int,
//        onResult: (Result<Unit>) -> Unit,
//    )
//
//    fun decreaseQuantity(
//        productId: Long,
//        onResult: (Result<Unit>) -> Unit,
//    )
//
//    fun deleteProduct(
//        productId: Long,
//        onResult: (Result<Unit>) -> Unit,
//    )
}
