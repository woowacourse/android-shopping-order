package woowacourse.shopping.data.cart

interface CartRemoteDataSource {
    fun addProduct(productId: Int)
    fun deleteCartProduct(productId: Int)
    fun updateProductCount(cartProductInfo: CartLocalDataModel)
    fun getAllCartProductsInfo(): List<CartRemoteDataModel>
}
