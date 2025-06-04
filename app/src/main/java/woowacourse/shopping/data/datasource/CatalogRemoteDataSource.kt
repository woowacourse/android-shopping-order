package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse

interface CatalogRemoteDataSource {
    fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchAllProducts(
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchProductDetail(
        id: Long,
        onSuccess: (Content) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
