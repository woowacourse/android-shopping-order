package woowacourse.shopping.data.respository.product

import woowacourse.shopping.data.model.ProductEntity

interface ProductRepository {
    fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<ProductEntity>) -> Unit,
    )
    fun loadDataById(id: Long): ProductEntity
}
