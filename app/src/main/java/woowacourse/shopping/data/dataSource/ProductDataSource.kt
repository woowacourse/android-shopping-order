package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.remote.dto.ProductDto
import woowacourse.shopping.data.remote.dto.ProductListDto
import woowacourse.shopping.domain.util.WoowaResult

interface ProductDataSource {
    fun fetchProduct(id: Int, callback: (WoowaResult<ProductDto>) -> Unit)
    fun fetchPagedProducts(
        pageItemCount: Int,
        lastId: Int,
        callback: (result: WoowaResult<ProductListDto>, isLast: Boolean) -> Unit,
    )
}
