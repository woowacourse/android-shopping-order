package woowacourse.shopping.data.datasource.product

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductsDto
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.product.RetrofitProductService
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSource(
    private val productService: RetrofitProductService,
) : ProductDataSource {

    override fun requestProducts(
        page: Int,
        size: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = productService.requestProducts(page, size)
        call.enqueue(object : retrofit2.Callback<ProductsDto> {
            override fun onResponse(
                call: Call<ProductsDto>,
                response: Response<ProductsDto>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val products = result?.products?.map { it.toDomain() }
                    products?.let { onSuccess(it) }
                    Log.d("test", "retrofit result: $result")
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductsDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun requestProductById(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = productService.requestProductById(productId)
        call.enqueue(object : retrofit2.Callback<ProductDto?> {
            override fun onResponse(
                call: Call<ProductDto?>,
                response: Response<ProductDto?>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("test", "retrofit requestProductById result: $result")
                    if (result != null) {
                        onSuccess(result.toDomain())
                    }
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto?>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun insertProduct(
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = productService.insertProduct(product)

        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun updateProduct(
        productId: String,
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = productService.updateProduct(productId, product)
        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val call = productService.deleteProduct(productId)
        call.enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(
                call: Call<ProductDto>,
                response: Response<ProductDto>,
            ) {
                if (response.isSuccessful) {
                    Log.d("test", "retrofit requestProductById result: $response")
                    onSuccess()
                } else {
                    Log.d("test", "retrofit 실패")
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }
}
