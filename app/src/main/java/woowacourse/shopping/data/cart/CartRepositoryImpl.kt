package woowacourse.shopping.data.cart

import com.example.domain.CartProduct
import com.example.domain.Product
import com.example.domain.repository.CartRepository

class CartRepositoryImpl(
    private val url: String,
    private val service: CartRemoteService
) : CartRepository {

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestAllProducts(
                url = url,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }.start()
    }

    override fun addCartProduct(
        productId: Int,
        onSuccess: (cartId: Int) -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateCartProductQuantity(
        id: Product,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteCartProduct(
        id: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
