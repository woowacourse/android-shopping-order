package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.NetworkResult
import woowacourse.shopping.domain.Product

interface ProductRepository {
    fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    )

    fun load(
        startPage: Int,
        pageSize: Int,
        callBack: (result: NetworkResult<List<Product>>) -> Unit,
    )

    fun loadById(
        id: Long,
        callback: (result: NetworkResult<Product>) -> Unit,
    )
}
