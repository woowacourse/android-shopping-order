package woowacourse.shopping.data.remote

import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.model.Product

class ProductRetrofitDataSource : ProductRemoteDataSource {

    override fun getAll(): Result<List<Product>> = runCatching {
        RetrofitClient.getInstance().retrofitProductService
            .getProducts().execute().body()!!.map { it.toDomain() }
    }

    override fun findById(id: Int): Result<Product> = runCatching {
        RetrofitClient.getInstance().retrofitProductService
            .getProduct(id).execute().body()!![0].toDomain()
    }
}
