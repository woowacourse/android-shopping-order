package woowacourse.shopping

import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.model.ProductModel
import woowacourse.shopping.ui.model.ShoppingProductModel
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.URL
import woowacourse.shopping.ui.model.OrderHistoryModel
import java.time.LocalDateTime

fun createProductModel(): ProductModel = ProductModel(0, "", "글로", 1000)
fun createShoppingProductModel(): ShoppingProductModel =
    ShoppingProductModel(createProductModel(), 1)
fun createCartProductModel(
    id: Int = 0,
    quantity: Int = 0,
    isChecked: Boolean = true,
    product: ProductModel = createProductModel()
): CartProductModel = CartProductModel(id, quantity, isChecked, product)
fun createOrderHistoryModel(
    id: Int = 0,
    price: Int = 1000,
    quantity: Int = 0,
    name: String = "히스토리"
): OrderHistoryModel = OrderHistoryModel(id, price, quantity, name)

fun createProduct(): Product = Product(0, URL(""), "글로", 1000)
fun createRecentProduct(): RecentProduct =
    RecentProduct(LocalDateTime.now(), createProduct())
fun createCartProduct(
    id: Int = 0,
    quantity: Int = 0,
    isChecked: Boolean = true,
    product: Product = createProduct()
): CartProduct = CartProduct(id, quantity, isChecked, product)
fun createOrderHistory(
    id: Int = 0,
    price: Int = 1000,
    quantity: Int = 0,
    name: String = "히스토리"
): OrderHistory = OrderHistory(id, price, quantity, name)
