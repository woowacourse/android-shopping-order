package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.CartItem

interface CartDataSource {
//    fun getCartProductCount(): Int
//
//    fun getTotalQuantity(): Int?
//
//    fun getQuantityById(productId: Long): Int

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
//    fun deleteProductById(productId: Long)
}
