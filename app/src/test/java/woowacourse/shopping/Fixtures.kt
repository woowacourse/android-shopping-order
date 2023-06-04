package woowacourse.shopping

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.URL
import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.model.OrderHistoryModel
import woowacourse.shopping.ui.model.OrderModel
import woowacourse.shopping.ui.model.ProductModel
import woowacourse.shopping.ui.model.ShoppingProductModel
import java.time.LocalDateTime

fun createProductModel(): ProductModel = ProductModel(0, "", "글로", 1000)
fun createShoppingProductModel(): ShoppingProductModel =
    ShoppingProductModel(createProductModel(), 1)
fun createCartProductModel(
    id: Int = 1,
    quantity: Int = 10,
    isChecked: Boolean = true,
    product: ProductModel = createProductModel()
): CartProductModel = CartProductModel(id, quantity, isChecked, product)
fun createOrderHistoryModel(
    id: Int = 0,
    price: Int = 1000,
    quantity: Int = 0,
    name: String = "히스토리"
): OrderHistoryModel = OrderHistoryModel(id, price, quantity, name)
fun createOrderModel(
    products: List<CartProductModel> = listOf(createCartProductModel()),
    originalPrice: Int = 5000,
    usedPoints: Int = 1000,
    finalPrice: Int = 4000
): OrderModel = OrderModel(products, originalPrice, usedPoints, finalPrice)

fun createProduct(): Product = Product(0, URL(""), "글로", 1000)
fun createRecentProduct(): RecentProduct =
    RecentProduct(LocalDateTime.now(), createProduct())
fun createCartProduct(
    id: Int = 1,
    quantity: Int = 10,
    isChecked: Boolean = true,
    product: Product = createProduct()
): CartProduct = CartProduct(id, quantity, isChecked, product)
fun createOrderHistory(
    id: Int = 0,
    price: Int = 1000,
    quantity: Int = 0,
    name: String = "히스토리"
): OrderHistory = OrderHistory(id, price, quantity, name)
fun createOrder(
    products: List<CartProduct> = listOf(createCartProduct()),
    originalPrice: Int = 5000,
    usedPoints: Int = 1000,
    finalPrice: Int = 4000
): Order = Order(products, originalPrice, usedPoints, finalPrice)
