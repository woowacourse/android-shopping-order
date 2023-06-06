package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.mapper.ProductMapper.toDomain
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.Product

class ProductRemoteDataSourceRetrofit(retrofit: Retrofit) : ProductRemoteDataSource {
    private val productService: ProductService = retrofit.create(ProductService::class.java)

    override fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        productService.requestProducts().enqueue(object : Callback<List<ProductEntity>> {
            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: () -> Unit) {
        productService.requestProduct(id).enqueue(object : Callback<ProductEntity> {
            override fun onResponse(call: Call<ProductEntity>, response: Response<ProductEntity>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<ProductEntity>, t: Throwable) {
                onFailure()
            }
        })
    }
}