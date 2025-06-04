package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface ProductsRepository {
    fun getProducts(
        page: Int,
        size: Int = 20,
        onResult: (Result<PagingData>) -> Unit,
    )

    fun getProductById(
        id: Long,
        onResult: (Result<ProductUiModel>) -> Unit,
    )

    fun getRecommendProducts(
        category: String,
        onResult: (Result<List<Product>>) -> Unit,
    )
}
