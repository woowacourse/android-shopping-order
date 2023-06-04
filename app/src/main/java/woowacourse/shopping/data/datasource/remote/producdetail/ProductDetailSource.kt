package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.response.ProductResponseDto

interface ProductDetailSource {
    fun getById(id: Long): Result<ProductResponseDto>
}
