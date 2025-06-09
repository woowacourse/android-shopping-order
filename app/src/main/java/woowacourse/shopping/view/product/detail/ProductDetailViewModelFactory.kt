package woowacourse.shopping.view.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductByProductIdUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase

class ProductDetailViewModelFactory(
    private val product: Product,
    private val recentProductRepository: RecentProductRepository,
    private val getCartProductByProductIdUseCase: GetCartProductByProductIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(
                product,
                recentProductRepository,
                getCartProductByProductIdUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
