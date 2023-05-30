package woowacourse.shopping.data.repository

import android.os.Looper
import android.util.Log
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.datasource.remote.product.ProductDataSource
import woowacourse.shopping.mapper.toDomain

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
) : ProductRepository {

    private val products = mutableListOf<Product>()
    private var isProductDataCached = false

    override fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (isProductDataCached) {
            onSuccess(products)
            return
        }

        productDataSource.getAllProducts().enqueue(
            createResponseCallback(
                onSuccess = {
                    val productsDomain = it.map { productDto -> productDto.toDomain() }
                    products.clear()
                    products.addAll(productsDomain)
                    isProductDataCached = true
                    onSuccess(productsDomain)
                },
                onFailure,
            ),
        )
    }

    override fun getMoreProducts(
        offset: Int,
        count: Int,
        onSuccess: (List<Product>) -> Unit,
    ) {
        if (isProductDataCached) {
            val limitedProducts = products.subList(
                offset.coerceAtMost(products.size),
                (offset + count).coerceAtMost(products.size),
            )
            onSuccess(limitedProducts)
        } else {
            getAllProducts(
                onSuccess = { products ->
                    val limitedProducts = products.subList(
                        offset.coerceAtMost(products.size),
                        (offset + count).coerceAtMost(products.size),
                    )
                    onSuccess(limitedProducts)
                },
                onFailure = { exception ->
                    Log.e("ProductRepositoryImpl", "getMoreProducts: $exception")
                },
            )
        }
    }

    private inline fun <reified T> createResponseCallback(
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (Exception) -> Unit,
    ): retrofit2.Callback<T> {
        val handler = android.os.Handler(Looper.getMainLooper())

        return object : retrofit2.Callback<T> {
            override fun onResponse(call: retrofit2.Call<T>, response: retrofit2.Response<T>) {
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

            override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }
    }
}
