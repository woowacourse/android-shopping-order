package woowacourse.shopping.data.datasource.remote.producdetail

import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDetailRemoteSource {
    fun getById(id: Long, callback: (Result<ProductDTO>) -> Unit)
}
