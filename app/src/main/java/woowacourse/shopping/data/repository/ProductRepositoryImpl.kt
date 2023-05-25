package woowacourse.shopping.data.repository

import android.os.Looper
import android.util.Log
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import woowacourse.shopping.data.datasource.remote.product.ProductDataSource
import java.io.IOException
import java.util.concurrent.CountDownLatch

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    private val allProducts: MutableList<Product> = mutableListOf()
    private val latch = CountDownLatch(1)

    override fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        productDataSource.getAllProducts().enqueue(
            createResponseCallback(
                onSuccess,
                onFailure,
            ),
        )
        // latch.await()
    }

    private fun createResponseCallback2() = object : Callback {
        override fun onResponse(call: Call, response: Response) {
            // 콜백 수정
            if (response.isSuccessful) {
                Thread {
                    allProducts.addAll(parseToObject(response.body?.string()))
                    latch.countDown()
                }.start()
                return
            }
            Log.e("LoginRepositoryImpl : isFailure", response.message)
        }

        override fun onFailure(call: Call, e: IOException) {
            Log.e("LoginRepositoryImpl : onFailure", e.message.toString())
        }
    }

    private fun parseToObject(responseBody: String?): List<Product> {
        val listType = object : TypeToken<List<Product>>() {}.type
        return Gson().fromJson(responseBody, listType)
    }

    override fun getMoreProducts(unit: Int, previousSize: Int): List<Product> {
        return when (allProducts.size > unit) {
            true -> {
                val nextProducts = allProducts.drop(previousSize)
                return nextProducts.take(unit)
            }

            false -> allProducts.toList()
        }
    }

    override fun getAll(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getNext(
        count: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun findProductById(id: Long) {
        TODO("Not yet implemented")
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
