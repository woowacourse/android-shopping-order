package woowacourse.shopping.data.repository

import android.os.Looper
import com.example.domain.model.Product
import com.example.domain.repository.ProductDetailRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import woowacourse.shopping.data.datasource.remote.producdetail.ProductDetailSource
import java.io.IOException

class ProductDetailRepositoryImpl(
    private val productDetailSource: ProductDetailSource,
): ProductDetailRepository {
    override fun getById(id: Long, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        productDetailSource.getById(id).enqueue(
            createResponseCallback(onSuccess, onFailure),
        )
    }

    private inline fun <reified T> createResponseCallback(
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Exception) -> Unit,
    ): Callback {
        val handler = android.os.Handler(Looper.getMainLooper())
        return object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Thread {
                        val result = parseToObject<T>(response.body?.string())
                        handler.post {
                            onSuccess.invoke(result)
                        }
                    }.start()
                    return
                }
                handler.post {
                    onFailure.invoke(Exception("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    onFailure.invoke(e)
                }
            }
        }
    }

    private inline fun <reified T> parseToObject(responseBody: String?): T {
        return Gson().fromJson(responseBody, object : TypeToken<T>() {}.type)
    }
}
