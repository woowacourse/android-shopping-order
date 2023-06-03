package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductListDto
import woowacourse.shopping.domain.util.WoowaResult

interface ProductDataSource {
    fun fetchProduct(id: Long, callback: (WoowaResult<ProductDto>) -> Unit)
    fun fetchPagedProducts(
        pageItemCount: Int,
        lastId: Long,
        callback: (result: WoowaResult<ProductListDto>) -> Unit,
    )
}
