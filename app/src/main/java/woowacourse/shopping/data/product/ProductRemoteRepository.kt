package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.domain.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRemoteRepository : ProductRepository {

    private val productRemoteService: ProductRemoteService = ProductRemoteService.getInstance()

    override fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        productRemoteService.requestProducts()
            .enqueue(object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: Call<List<ProductDto>>,
                    response: Response<List<ProductDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val products = response.body()?.map { it.toDomain() } ?: return
                    val slicedProducts = products.slice(offset until products.size)
                        .take(limit)
                    onFinish(slicedProducts)
                }

                override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                }
            })
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        productRemoteService.requestProducts()
            .enqueue(object : retrofit2.Callback<List<ProductDto>> {
                override fun onResponse(
                    call: Call<List<ProductDto>>,
                    response: Response<List<ProductDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val products = response.body()?.map { it.toDomain() } ?: return
                    onFinish(products.size)
                }

                override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                }
            })
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        productRemoteService.requestProduct(id).enqueue(object : retrofit2.Callback<ProductDto> {
            override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                if (response.isSuccessful.not()) return
                val product = response.body()?.toDomain() ?: return
                onFinish(product)
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
            }
        })
    }
}
