package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage

interface ProductRepository {
    suspend fun loadProducts(page: Int): ResponseResult<ProductsPage>

    suspend fun loadProduct(id: Long): ResponseResult<Product>
}
