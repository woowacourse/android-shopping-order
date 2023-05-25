package woowacourse.shopping.data.service.cart

import okhttp3.RequestBody.Companion.toRequestBody
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.model.CartProduct
import woowacourse.shopping.data.model.ProductCount
import woowacourse.shopping.data.util.convertProductIdToJson
import woowacourse.shopping.data.util.okhttp.Header
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient
import woowacourse.shopping.data.util.toCartProducts
import woowacourse.shopping.data.util.toJson

class CartServiceImpl : CartService {
    private val baseUrl: String = pref.getBaseUrl().toString()

    override fun getAllCartProduct(): List<CartProduct> {
        val url = "$baseUrl/cart-items"
        val cartProducts = mutableListOf<CartProduct>()

        val latch = ShoppingOkHttpClient.enqueue(
            ShoppingOkHttpClient.get(url),
            onSuccess = { _, response ->
                response.body?.string()?.let { cartProducts.addAll(it.toCartProducts()) }
            },
            onFailed = { _, _ -> },
        )

        latch.await()
        return cartProducts
    }

    override fun addCartProductByProductId(productId: ProductId) {
        val url = "$baseUrl/cart-items"

        val latch = ShoppingOkHttpClient.enqueue(
            ShoppingOkHttpClient.post(
                url,
                convertProductIdToJson(productId).toRequestBody(Header.JSON_MEDIA_TYPE)
            ),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun updateProductCountById(cartProductId: Int, count: ProductCount) {
        val url = "$baseUrl/cart-items/$cartProductId"

        val latch = ShoppingOkHttpClient.enqueue(
            ShoppingOkHttpClient.patch(url, count.toJson().toRequestBody(Header.JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun deleteCartProductById(cartProductId: Int) {
        val url = "$baseUrl/cart-items/$cartProductId"

        val latch = ShoppingOkHttpClient.enqueue(
            ShoppingOkHttpClient.delete(url),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun findCartProductByProductId(productId: Int): CartProduct? {
        val cartProducts = getAllCartProduct()
        return cartProducts.find { it.product.id == productId }
    }
}
