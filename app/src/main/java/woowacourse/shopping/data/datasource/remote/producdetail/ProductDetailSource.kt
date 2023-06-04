package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.response.ProductResponseDTO

interface ProductDetailSource {
    fun getById(id: Long): Result<ProductResponseDTO>
}
