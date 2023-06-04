package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.model.Product
import woowacourse.shopping.utils.RetrofitUtil

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getAll(): Result<List<Product>> = runCatching {
        RetrofitUtil.getInstance().retrofitProductService
            .getProducts().execute().body()!!
    }

    override fun getNext(count: Int): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun insert(product: Product): Result<Int> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): Result<Product> = runCatching {
        RetrofitUtil.getInstance().retrofitProductService
            .getProduct(id).execute().body()!![0]
    }
}
