package woowacourse.shopping.data.datasource.product.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.remote.OkHttpModule
import java.io.IOException
import java.lang.Integer.min

class RemoteProductDataSource() : ProductDataSource.Remote {

    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<DataProduct>) -> Unit
    ) {
        val url = "${OkHttpModule.BASE_URL}/products"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val productCache = OkHttpModule.gson.fromJson(
                    response.body?.string(),
                    Array<DataProduct>::class.java
                ).toList()
                onReceived(getDataProductsFromCache(size, lastId, productCache))
            }
        })
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<DataProduct>
    ): List<DataProduct> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId }
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }
}
