package woowacourse.shopping.model.data.db

import com.shopping.domain.Product

interface ProductService {
    fun request(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )
}
