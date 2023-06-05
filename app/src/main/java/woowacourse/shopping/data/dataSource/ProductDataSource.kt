package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.ProductDto

interface ProductDataSource {
    fun getAll(callback: (List<ProductDto>?) -> Unit)
    fun findById(id: Int, callback: (ProductDto?) -> Unit)
}
