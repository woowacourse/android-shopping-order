package woowacourse.shopping.data.datasource.cart

import retrofit2.Call
import woowacourse.shopping.data.dto.CartProductDto

interface CartProductDataSource {
    fun requestCartProducts(token: String): Call<List<CartProductDto>>

    fun addCartProductByProductId(token: String, productId: String): Call<Int>

    fun updateCartProductCountById(token: String, cartItemId: String, quantity: Int): Call<Void>

    fun deleteCartProductById(token: String, cartItemId: String): Call<CartProductDto>

    // fun requestCartProductById(productId: Int): CartProduct?
}
