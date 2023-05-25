package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Products

interface ProductRepository {
    fun getProductsInSize(startIndex: Int, size: Int, onSuccess: (Products) -> Unit, onFailure: () -> Unit)
}
