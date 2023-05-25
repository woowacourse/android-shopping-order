package woowacourse.shopping.data.datasource.basket.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct
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

    override fun add(product: DataProduct, onReceived: (Int) -> Unit) {
        val url = "${OkHttpModule.BASE_URL}/cart-items"
        val requestBody = "{\"productId\":\"${product.id}\"}"
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val productId = response.headers.get("Location")?.split("/")?.last()?.toInt()

                productId?.let {
                    onReceived(it)
                }
            }
        })
    }

    override fun update(basketProduct: DataBasketProduct) {
        val url = "${OkHttpModule.BASE_URL}/cart-items/${basketProduct.id}"
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
        val url = "${OkHttpModule.BASE_URL}/cart-items/${basketProduct.id}"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {}
        })
    }
}
