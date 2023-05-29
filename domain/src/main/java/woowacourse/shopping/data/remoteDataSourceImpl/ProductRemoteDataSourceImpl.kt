package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.model.Product
import woowacourse.shopping.utils.RetrofitUtil

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getAll(callback: (Result<List<Product>>) -> Unit) {
        RetrofitUtil.retrofitProductService.getProducts().enqueue(RetrofitUtil.callback(callback))
    }

    override fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun insert(product: Product, callback: (Result<Int>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int, callback: (Result<Product>) -> Unit) {
        RetrofitUtil.retrofitProductService.getProduct(id).enqueue(RetrofitUtil.callback(callback))
    }
}
