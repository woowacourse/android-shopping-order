package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.datasource.response.ProductEntity

interface ProductRemoteDataSource {

    fun getPartially(
        size: Int,
        lastId: Int,
    ): Result<List<ProductEntity>>
}
