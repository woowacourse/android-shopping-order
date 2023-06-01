package woowacourse.shopping.data.server

import woowacourse.shopping.domain.Product

interface ProductRemoteDataSource {
    fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit)

    fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: () -> Unit)
}