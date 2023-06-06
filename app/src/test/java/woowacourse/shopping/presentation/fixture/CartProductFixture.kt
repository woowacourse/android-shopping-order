package woowacourse.shopping.presentation.fixture

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.UnCheckableCartProductModel

object CartProductFixture {
    fun getCartProducts(vararg ids: Long) = ids.map { getCartProduct(it) }

    fun getCartProduct(id: Long) = CartProduct(id, getProduct(id), 1, true)

    fun getCartProductModels(vararg ids: Long) = ids.map { getCartProductModel(it) }

    fun getCartProductModel(id: Long): CartProductModel =
        UnCheckableCartProductModel(id, getProductModel(id), 1)

    fun getProducts(vararg ids: Long) = ids.map { getProduct(it) }

    fun getProductModels(vararg ids: Long) = ids.map { getProductModel(it) }

    fun getProduct(id: Long) = Product(id, "text.com", "test", Price(1000))

    fun getProductModel(id: Long) = ProductModel(id, "text.com", "test", 1000)
}
