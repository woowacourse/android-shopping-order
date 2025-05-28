package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface ProductsRepository {
    fun getProducts(
        page: Int,
        pageSize: Int = 20,
        onResult: (Result<PagingData>) -> Unit,
    )

    fun getProductById(
        id: Int,
        onResult: (Result<ProductUiModel>) -> Unit,
    )
}
