package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.remote.response.ProductResponseDTO

interface ProductDataSource {

    fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductResponseDTO>>
}
