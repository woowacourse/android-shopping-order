package woowacourse.shopping.data.cart

interface CartRemoteDataSource {
    fun addProduct(productId: Int)
    fun deleteCartProduct(cartId: Int)
    fun updateProductCount(cartId: Int, count: Int)
    fun getAllCartProductsInfo(): List<CartRemoteDataModel>
}
