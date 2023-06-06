package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.model.Product

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getAll(): Result<List<Product>> = runCatching {
        RetrofitClient.getInstance().retrofitProductService
            .getProducts().execute().body()!!.map { it.toDomain() }
    }

    override fun getNext(count: Int): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun insert(product: Product): Result<Int> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): Result<Product> = runCatching {
        RetrofitClient.getInstance().retrofitProductService
            .getProduct(id).execute().body()!![0].toDomain()
    }
}
