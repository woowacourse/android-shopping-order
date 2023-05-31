package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.ShoppingProducts

interface ProductRepository {
    fun getProducts(onSuccess: (ShoppingProducts) -> Unit, onFailure: () -> Unit)
}
