package woowacourse.shopping.data.cart.local

import woowacourse.shopping.data.cart.CartLocalDataModel

interface CartLocalDataSource {
    fun addProduct(productId: Int)
    fun deleteCartProduct(productId: Int)
    fun updateProductCount(cartProductInfo: CartLocalDataModel)
    fun getProductsInfo(limit: Int, offset: Int): List<CartLocalDataModel>
    fun getAllProductsInfo(): List<CartLocalDataModel>
    fun getProductInfoById(id: Int): CartLocalDataModel?
}
