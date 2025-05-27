package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductsRepository {
    fun getProducts(onResult: (Result<List<Product>>) -> Unit)

    fun getProductById(
        id: Int,
        onResult: (Result<Product>) -> Unit,
    )
}