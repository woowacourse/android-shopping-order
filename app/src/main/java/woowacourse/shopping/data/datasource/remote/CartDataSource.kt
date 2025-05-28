package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.CartItem

interface CartDataSource {
    fun getTotalCount(onResult: (Result<Int>) -> Unit)

    fun getPagedCartProducts(
        page: Int,
        size: Int,
        onResult: (List<CartItem>) -> Unit,
    )

//    fun existsByProductId(productId: Long): Boolean
//
//    fun increaseQuantity(
//        productId: Long,
//        quantity: Int,
//    )
//
//    fun decreaseQuantity(productId: Long)
//
//    fun insertProduct(cartEntity: CartEntity)
//
    fun insertProduct(
        productId: Long,
        quantity: Int,
        onResult: (Result<Long>) -> Unit,
    )

//    fun deleteProductById(productId: Long)
}
