package woowacourse.shopping.view.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.GetCartProductByProductIdUseCase
import woowacourse.shopping.domain.usecase.UpdateQuantityUseCase

class ProductDetailViewModelFactory(
    private val product: Product,
    private val recentProductRepository: RecentProductRepository,
    private val getCartProductByProductIdUseCase: GetCartProductByProductIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(
                product,
                recentProductRepository,
                getCartProductByProductIdUseCase,
                addToCartUseCase,
                updateQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
