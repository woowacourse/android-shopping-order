package woowacourse.shopping.data.datasource.basket.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.remote.OkHttpModule
import java.io.IOException

class RemoteBasketDataSource : BasketDataSource.Remote {
    override fun getAll(onReceived: (List<DataBasketProduct>) -> Unit) {
        val url = "${OkHttpModule.BASE_URL}/cart-items"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                onReceived(
                    OkHttpModule.gson.fromJson(
                        response.body?.string(),
                        Array<DataBasketProduct>::class.java
                    ).toList()
                )
            }
        })
    }

    override fun add(basketProduct: DataBasketProduct) {
        val url = "${OkHttpModule.BASE_URL}/cart_items"
        val requestBody = "{\"productId\":\"${basketProduct.product.id}\"}"
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }

    override fun minus(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun update(basketProduct: DataBasketProduct) {
        val url = "${OkHttpModule.BASE_URL}/cart_items/${basketProduct.id}"
        val requestBody = "{\"quantity\":\"${basketProduct.count.value}\"}"
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {}
        })
    }

    override fun remove(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }
}
