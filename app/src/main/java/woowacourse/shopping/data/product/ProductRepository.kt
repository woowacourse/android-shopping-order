package woowacourse.shopping.data.product

import com.example.domain.Pagination
import com.example.domain.product.Product

interface ProductRepository {
    fun requestFetchProductsUnit(
        unitSize: Int,
        page: Int,
        success: (List<Product>, Pagination) -> Unit,
        failure: () -> Unit
    )

    fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    )
}
