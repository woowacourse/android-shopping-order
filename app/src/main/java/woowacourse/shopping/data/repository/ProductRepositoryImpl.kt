package woowacourse.shopping.data.repository

import com.example.domain.cache.ProductCache
import com.example.domain.model.BaseResponse
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.remote.ProductService
import woowacourse.shopping.data.model.dto.response.ProductDto

class ProductRepositoryImpl(
    private val service: ProductService,
    private val cache: ProductCache?,
) : ProductRepository {
    private val allProducts: MutableList<Product> = mutableListOf()
    override fun fetchFirstProducts(
        callBack: (BaseResponse<List<Product>>) -> Unit,
    ) {
        if (cache?.productList?.isEmpty() == true) {
            service.getAllProducts().enqueue(object : Callback<List<ProductDto>> {
                override fun onResponse(
                    call: Call<List<ProductDto>>,
                    response: Response<List<ProductDto>>
                ) {
                    val products = response.body() ?: emptyList()
                    allProducts.clear()
                    allProducts.addAll(
                        products.map {
                            with(it) {
                                Product(id, name, imageUrl, Price(price))
                            }
                        }
                    )
                    val nextProducts = allProducts.take(UNIT_SIZE)
                    cache.addProducts(nextProducts)
                    callBack(BaseResponse.SUCCESS(nextProducts))
                }

                override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                    callBack(BaseResponse.NETWORK_ERROR())
                }
            })
        } else {
            callBack(BaseResponse.SUCCESS(cache?.productList ?: emptyList()))
        }
    }

    override fun fetchNextProducts(
        lastProductId: Long,
        callBack: (BaseResponse<List<Product>>) -> Unit,
    ) {
        val nextProducts = allProducts.filter { it.id > lastProductId }.take(UNIT_SIZE)
        cache?.addProducts(nextProducts)
        callBack(BaseResponse.SUCCESS(nextProducts))
    }

    override fun fetchProductById(
        productId: Long,
        callBack: (BaseResponse<Product>) -> Unit,
    ) {
        service.getProductById(productId).enqueue(object : Callback<ProductDto> {
            override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                // todo
                val product = response.body() ?: return callBack(BaseResponse.FAILED(200, ""))
                callBack(
                    BaseResponse.SUCCESS(
                        with(product) {
                            Product(id, name, imageUrl, Price(price))
                        }
                    )
                )
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    companion object {
        private const val UNIT_SIZE = 20
    }
}
