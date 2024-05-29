/*
package woowacourse.shopping.data.database.product

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.database.retrofitService
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.service.ProductService

class RetrofitProductService : ProductService {
    private val products = mutableListOf<Product>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            retrofitService.requestProducts().enqueue(
                object : Callback<ProductResponseDto> {
                    override fun onResponse(
                        call: Call<ProductResponseDto>,
                        response: Response<ProductResponseDto>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            println("body : $body")
                            body?.let {
                                products.addAll(it.content.map { productDto -> productDto.toDomainModel() })
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponseDto>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
        }
    }

    override fun fetchProductsSize(): Int {
        return products.size
    }

    override fun findAll(): List<Product> {
        return products
    }

    override fun findProductById(productId: Long): Product? {
        TODO("Not yet implemented")
    }

    override fun loadPagingProducts(
        offset: Int,
        pagingSize: Int,
    ): List<Product> {
        TODO("Not yet implemented")
    }
}
*/
