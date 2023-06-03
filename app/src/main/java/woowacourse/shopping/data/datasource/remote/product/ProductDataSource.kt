package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDataSource {

    fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductDTO>>
}
