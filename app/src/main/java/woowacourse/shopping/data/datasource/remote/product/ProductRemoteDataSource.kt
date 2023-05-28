package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product

interface ProductRemoteDataSource {
    fun requestProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit)
}
