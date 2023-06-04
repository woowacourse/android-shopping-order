package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.remote.response.ProductResponseDto

interface ProductDataSource {

    fun getSubListProducts(limit: Int, scrollCount: Int): Result<List<ProductResponseDto>>
}
