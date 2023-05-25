package woowacourse.shopping.data.service

import okhttp3.RequestBody.Companion.toRequestBody
import woowacourse.shopping.data.model.CartProduct
import woowacourse.shopping.data.model.ProductCount
import woowacourse.shopping.data.util.convertProductIdToJson
import woowacourse.shopping.data.util.okhttp.Header.Companion.JSON_MEDIA_TYPE
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.delete
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.get
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.patch
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.post
import woowacourse.shopping.data.util.toCartProducts
import woowacourse.shopping.data.util.toJson
import woowacourse.shopping.server.BASE_URL

class CartServiceImpl : CartService {
    override fun getAllCartProduct(): List<CartProduct> {
        val url = "$BASE_URL/cart-items"
        val cartProducts = mutableListOf<CartProduct>()

        val latch = ShoppingOkHttpClient.enqueue(
            get(url),
            onSuccess = { _, response ->
                response.body?.string()?.let { cartProducts.addAll(it.toCartProducts()) }
            },
            onFailed = { _, _ -> },
        )

        latch.await()
        return cartProducts
    }

    override fun addCartProductByProductId(productId: ProductId) {
        val url = "$BASE_URL/cart-items"

        val latch = ShoppingOkHttpClient.enqueue(
            post(url, convertProductIdToJson(productId).toRequestBody(JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun updateProductCountById(cartProductId: Int, count: ProductCount) {
        val url = "$BASE_URL/cart-items/$cartProductId"

        val latch = ShoppingOkHttpClient.enqueue(
            patch(url, count.toJson().toRequestBody(JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun deleteCartProductById(cartProductId: Int) {
        val url = "$BASE_URL/cart-items/$cartProductId"

        val latch = ShoppingOkHttpClient.enqueue(
            delete(url),
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
