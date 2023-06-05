package woowacouse.shopping.data.repository.product

import woowacouse.shopping.model.product.Product

interface ProductRepository {
    fun loadDatas(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<Product>) -> Unit,
    )
    fun loadDataById(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (products: Product) -> Unit,
    )
}
