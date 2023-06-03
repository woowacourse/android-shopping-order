package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDetailSource {
    fun getById(id: Long): Result<ProductDTO>
}
