package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductRemoteDataSource {

    fun getSubListProducts(limit: Int, scrollCount: Int, callback: (Result<List<ProductDTO>>) -> Unit)
}
