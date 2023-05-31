package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.data.entity.ProductEntity.Companion.toDomain
import woowacourse.shopping.domain.product.Product

class ProductRemoteService(retrofit: Retrofit) : ProductDataSource {
    private val productService = retrofit.create(ProductRetrofitService::class.java)

    override fun findAll(onFinish: (List<Product>) -> Unit) {
        productService.selectProducts().enqueue(object : Callback<List<ProductEntity>> {
            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                if (response.code() != 200) return
                onFinish(response.body()?.map { it.toDomain() } ?: return)
            }
        })
    }

    override fun findRanged(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        productService.selectProducts().enqueue(object : Callback<List<ProductEntity>> {
            override fun onFailure(call: Call<List<ProductEntity>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<List<ProductEntity>>,
                response: Response<List<ProductEntity>>
            ) {
                if (response.code() != 200) return
                val products = response.body() ?: return
                val rangedProducts = products.slice(offset until products.size).take(limit)
                onFinish(rangedProducts.map { it.toDomain() })
            }
        })
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        findAll {
            onFinish(it.size)
        }
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        productService.selectProduct(id).enqueue(object : Callback<ProductEntity> {
            override fun onFailure(call: Call<ProductEntity>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ProductEntity>,
                response: Response<ProductEntity>
            ) {
                if (response.code() != 200) return
                onFinish(response.body()?.toDomain())
            }
        })
    }
}
