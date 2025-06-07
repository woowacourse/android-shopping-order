package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class PaymentViewModel(
    private val cartProductRepository: CartProductRepository,
) : ViewModel() {
    private var selectedProducts: List<CartProduct> = listOf()

    val totalPrice: Int get() = selectedProducts.sumOf { it.totalPrice }

    fun initSelectedProducts(selectedCartProducts: List<CartProduct>) {
        selectedProducts = selectedCartProducts
    }
}
