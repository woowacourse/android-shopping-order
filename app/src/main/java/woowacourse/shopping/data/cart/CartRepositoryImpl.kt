package woowacourse.shopping.data.cart

import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun putProductInCart(productId: Int) {
        cartRemoteDataSource.addProduct(productId)
    }

    override fun deleteCartProductId(productId: Int) {
        cartRemoteDataSource.deleteCartProduct(productId)
    }

    override fun updateCartProductCount(productId: Int, count: Int) {
        val cartLocalDataModel = CartLocalDataModel(productId, count)
        cartRemoteDataSource.updateProductCount(cartLocalDataModel)
    }

    override fun getAllCartProductsInfo(): CartProductInfoList {
        val cartDataModels = cartRemoteDataSource.getAllCartProductsInfo()
        return CartProductInfoList(
            cartDataModels.map {
                it.toDomain()
            }
        )
    }
}
