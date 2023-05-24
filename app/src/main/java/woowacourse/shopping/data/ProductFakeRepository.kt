package woowacourse.shopping.data

import android.os.Looper
import android.util.Log
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Thread.sleep

class ProductFakeRepository : ProductRepository {

    val ipAddress = "3.34.134.115"
    val port = 8080
    private val url = "http://$ipAddress:$port"
    private val okHttpClient = OkHttpClient()
    private var offset = 0

    override fun getAll(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val request = Request.Builder().url("$url/products").build()
        Log.d("request", request.toString())
        getResponse(request, onSuccess, onFailure)
    }

    override fun getNext(
        count: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        for (i in offset..offset + count) {
            findById(i.toLong(), { product ->
                onSuccess(listOf(product))
            }, onFailure)
        }
        offset += count
    }

    override fun findById(
        id: Long,
        onSuccess: (Product) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val request = Request.Builder().url("$url/products/$id").build()
        getResponse(request, { products ->
            val product = products.firstOrNull { it.id == id }
            if (product != null) {
                onSuccess(product)
            } else {
                onFailure(IllegalArgumentException("해당하는 아이템이 없습니다."))
            }
        }, onFailure)
    }

    private fun getResponse(
        request: Request,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val handler = android.os.Handler(Looper.getMainLooper())

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    onFailure(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Thread {
                        sleep(3000)
                        val responseBody = response.body?.string()
                        val products = ProductJsonParser.parse(responseBody)
                        handler.post {
                            onSuccess(products)
                        }
                    }.start()
                } else {
                    handler.post {
                        onFailure(Exception("Response unsuccessful"))
                    }
                }
            }
        })
    }
}
