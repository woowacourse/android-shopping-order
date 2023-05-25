package woowacourse.shopping.data.datasource.remote.shoppingcart

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.RequestBody
import woowacourse.shopping.data.remote.NetworkModule

class ShoppingCartDataSourceImpl : ShoppingCartDataSource {

    override fun getAllProductInCart(): Call {
        return NetworkModule.getService(CART_PATH)
    }

    override fun postProductToCart(productId: Long): Call {
        val requestBody: RequestBody = FormBody.Builder()
            .add("productId", "$productId")
            .build()

        return NetworkModule.postService(POST_PRODUCT_TO_CART, requestBody)
    }

    override fun patchProductCount(productId: Long, quantity: Int): Call {
        val requestBody: RequestBody = FormBody.Builder()
            .add("quantity", "$quantity")
            .build()

        return NetworkModule.patchService("$CART_PATH/$productId", requestBody)
    }

    override fun deleteProductInCart(productId: Long): Call {
        return NetworkModule.deleteService("$CART_PATH/$productId")
    }

    companion object {
        private const val CART_PATH = "/cart-items"
        private const val POST_PRODUCT_TO_CART = "/cart-items"
    }
}
