package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.ProductWithCartInfoDTO
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult

interface ProductDataSource {
    fun getProductsByRange(lastId: Int, pageItemCount: Int, callback: (DataResult<ProductsWithCartItemDTO>) -> Unit)
    fun getProductById(id: Int, callback: (DataResult<ProductWithCartInfoDTO>) -> Unit)
}
