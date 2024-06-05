package woowacourse.shopping.data.product

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun getProductById(
        productId: Long,
        callBack: (result: NetworkResult<Product>) -> Unit,
    )

    fun getProducts(
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    )

    fun getProductsByCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    )
}
