package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.cartItem.DefaultCartItemDataSource
import woowacourse.shopping.data.remote.repository.CartItemRepositoryImpl
import woowacourse.shopping.domain.CartItemRepository

object CartItemRepositoryInjector {
    var instance: CartItemRepository =
        CartItemRepositoryImpl(
            DefaultCartItemDataSource()
        )
        private set

    fun setInstance(cartItemRepository: CartItemRepository) {
        instance = cartItemRepository
    }
}
