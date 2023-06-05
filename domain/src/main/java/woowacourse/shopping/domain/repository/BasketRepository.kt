package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import java.util.concurrent.CompletableFuture

interface BasketRepository {

    fun getAll(): CompletableFuture<Result<List<BasketProduct>>>
    fun add(
        product: Product,
        onAdded: (Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun update(
        basketProduct: BasketProduct,
        onUpdated: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun remove(
        basketProduct: BasketProduct,
        onRemoved: () -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
