package woowacourse.shopping.data.repository

import android.os.Looper
import android.util.Log
import com.example.domain.model.Product
import com.example.domain.repository.ProductDetailRepository
import retrofit2.Call
import retrofit2.Callback
import woowacourse.shopping.data.datasource.remote.producdetail.ProductDetailSource
import woowacourse.shopping.mapper.toDomain

class ProductDetailRepositoryImpl(
    private val productDetailSource: ProductDetailSource,
) : ProductDetailRepository {
    override fun getById(id: Long, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        productDetailSource.getById(id).enqueue(
            createResponseCallback(onSuccess = {
                it.toDomain()
            }, onFailure),
        )
    }

    private inline fun <reified T> createResponseCallback(
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Exception) -> Unit,
    ): Callback<T> {
        val handler = android.os.Handler(Looper.getMainLooper())
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                val responseBody = response.body()
                if (responseBody != null) {
                    handler.post {
                        Log.d("ProductRepositoryImpl", "responseBody: $responseBody")
                        onSuccess(responseBody)
                    }
                } else {
                    handler.post {
                        onFailure(Exception("Response unsuccessful"))
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }
    }
}
