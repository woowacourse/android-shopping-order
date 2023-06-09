package woowacouse.shopping.data.repository.product

import woowacouse.shopping.model.product.Product

interface ProductRepository {
    fun loadDatas(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    )
    fun loadDataById(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: Product) -> Unit,
    )
}
