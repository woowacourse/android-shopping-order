package woowacourse.shopping.data.service

import com.example.domain.model.Product

interface ProductRemoteDataSource {
    fun requestProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit)
}
