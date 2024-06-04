package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.data.model.remote.ProductsDto

interface ProductDataSource {
    fun findProductById(id: Long): Result<ProductDto>

    fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<ProductsDto>

    companion object {
        private var instance: ProductDataSource? = null

        fun setInstance(productDataSource: ProductDataSource) {
            instance = productDataSource
        }

        fun getInstance(): ProductDataSource = requireNotNull(instance)
    }
}
