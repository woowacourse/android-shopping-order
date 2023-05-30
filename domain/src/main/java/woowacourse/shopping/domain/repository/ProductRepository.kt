package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.ShoppingProducts

interface ProductRepository {
    fun getProductsInSize(startIndex: Int, size: Int, onSuccess: (ShoppingProducts) -> Unit, onFailure: () -> Unit)
}
