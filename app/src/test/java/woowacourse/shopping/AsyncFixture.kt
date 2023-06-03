package woowacourse.shopping

import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import woowacourse.shopping.domain.user.Rank
import woowacourse.shopping.domain.user.User
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

fun <T> async(value: T): CompletableFuture<Result<T>> =
    CompletableFuture.completedFuture(Result.success(value))

fun User(
    id: Long = 0,
    email: String = "",
    password: String = "",
    token: String = "",
    rank: Rank = Rank.BRONZE
): User = User(id, email, password, token, rank)

fun Product(
    id: Long = 0,
    name: String = "",
    price: Int = 0,
    imageUrl: String = "",
): Product = Product(id, name, price, imageUrl)

fun CartItem(
    id: Long = 0,
    quantity: Int = 1,
    product: Product = Product()
): CartItem = CartItem(id, quantity, product)

fun Cart(
    value: Set<CartItem> = emptySet()
): Cart = Cart(value)

fun RecentlyViewedProduct(
    id: Long = 0,
    product: Product = Product(),
    viewedTime: LocalDateTime = LocalDateTime.now()
): RecentlyViewedProduct = RecentlyViewedProduct(id, product, viewedTime)
