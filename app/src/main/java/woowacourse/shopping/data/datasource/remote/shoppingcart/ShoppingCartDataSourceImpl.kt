package woowacourse.shopping.data.datasource.remote.shoppingcart

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.RequestBody
import woowacourse.shopping.data.remote.NetworkModule

class ShoppingCartDataSourceImpl : ShoppingCartDataSource {

    override fun getAllProductInCart(): Call {
        return NetworkModule.getService(GET_CART_PATH)
    }

    override fun postProductToCart(productId: Int): Call {
        val requestBody: RequestBody = FormBody.Builder()
            .add("productId", "$productId")
            .build()

        return NetworkModule.postService(POST_PRODUCT_TO_CART, requestBody)
    }

    override fun patchProductCount(quantity: Int): Call {
        TODO("Not yet implemented")
    }

    override fun deleteProductInCart(productId: Int): Call {
        TODO("Not yet implemented")
    }

    companion object {
        private const val GET_CART_PATH = "/cart-items"
        private const val POST_PRODUCT_TO_CART = "/cart-items"
    }
}
