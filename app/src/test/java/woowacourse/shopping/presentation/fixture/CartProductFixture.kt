package woowacourse.shopping.presentation.fixture

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.CheckableCartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.UnCheckableCartProductModel

object CartProductFixture {
    fun getCartProducts(vararg ids: Long) = ids.map { getCartProduct(it) }

    fun getCartProduct(id: Long, quantity: Int = 1) =
        CartProduct(id, getProduct(id), quantity, true)

    fun getCheckableCartProductModels(vararg ids: Long) =
        ids.map { getCheckableCartProductModel(it) }

    fun getCheckableCartProductModel(
        id: Long,
        quantity: Int = 1,
        isChecked: Boolean = true,
    ): CartProductModel =
        CheckableCartProductModel(id, getProductModel(id), quantity, isChecked = isChecked)

    fun getUncheckableCartProductModels(vararg ids: Long) =
        ids.map { getUncheckableCartProductModel(it) }

    fun getUncheckableCartProductModel(
        id: Long,
        quantity: Int = 1,
    ): CartProductModel =
        UnCheckableCartProductModel(id, getProductModel(id), quantity)

    fun getProducts(vararg ids: Long) = ids.map { getProduct(it) }

    fun getProductModels(vararg ids: Long) = ids.map { getProductModel(it) }

    fun getProduct(id: Long) = Product(id, "text.com", "test", Price(1000))

    fun getProductModel(id: Long) = ProductModel(id, "text.com", "test", 1000)
}
