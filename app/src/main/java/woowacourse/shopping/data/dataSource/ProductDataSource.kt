package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.ProductDto

interface ProductDataSource {
    fun getAll(
        onSuccess: (List<ProductDto>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun findById(
        id: Int,
        onSuccess: (ProductDto) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}
