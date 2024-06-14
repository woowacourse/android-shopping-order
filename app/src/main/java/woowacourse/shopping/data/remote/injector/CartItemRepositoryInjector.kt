package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.cartItem.RetrofitCartItemDataSource
import woowacourse.shopping.data.remote.repository.CartItemRepositoryImpl
import woowacourse.shopping.domain.repository.CartItemRepository

object CartItemRepositoryInjector {
    var instance: CartItemRepository =
        CartItemRepositoryImpl(
            RetrofitCartItemDataSource(),
        )
        private set

    fun setInstance(cartItemRepository: CartItemRepository) {
        instance = cartItemRepository
    }
}
