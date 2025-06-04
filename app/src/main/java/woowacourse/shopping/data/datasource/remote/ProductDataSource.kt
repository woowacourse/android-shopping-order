package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (Result<List<Product>>) -> Unit,
    )

    fun fetchProductById(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    )
}
