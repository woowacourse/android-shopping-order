package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Product
import java.util.concurrent.CompletableFuture

interface BasketRepository {

    fun getAll(): CompletableFuture<Result<List<BasketProduct>>>

    fun add(product: Product): CompletableFuture<Result<Int>>

    fun update(basketProduct: BasketProduct): CompletableFuture<Result<Unit>>

    fun remove(basketProduct: BasketProduct): CompletableFuture<Result<Unit>>
}
