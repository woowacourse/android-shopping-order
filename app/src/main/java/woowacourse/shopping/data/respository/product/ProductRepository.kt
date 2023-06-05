package woowacourse.shopping.data.respository.product

import com.example.domain.cart.CartProduct

interface ProductRepository {
    fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    )

    fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (product: CartProduct) -> Unit,
    )
}
