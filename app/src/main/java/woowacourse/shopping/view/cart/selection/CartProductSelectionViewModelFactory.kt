package woowacourse.shopping.view.cart.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductSelectionViewModelFactory(
    private val cartProductRepository: CartProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectionViewModel::class.java)) {
            return CartProductSelectionViewModel(cartProductRepository) as T
        }
        throw IllegalArgumentException(INVALID_VIEWMODEL_CLASS)
    }

    companion object {
        private const val INVALID_VIEWMODEL_CLASS: String = "생성할 수 없는 ViewModel 클래스입니다"
    }
}
