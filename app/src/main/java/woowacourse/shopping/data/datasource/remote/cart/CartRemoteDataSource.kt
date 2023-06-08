package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.FailureInfo

interface CartRemoteDataSource {
    fun loadAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun addCartProduct(
        productId: Int,
        onSuccess: (cartItemId: Int) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun updateCartProductCount(
        cartItemId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun deleteCartProduct(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun getCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct?) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )
}
