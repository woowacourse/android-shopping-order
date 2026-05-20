package woowacourse.shopping.data.repository.inmemory

import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.User

object InMemoryUserRepository {
    val STARTER =
        User(
            email = "todays-sun-day",
            password = "password",
            cart = Cart(emptyList()),
        )

    val SAM =
        User(
            email = "Redish03",
            password = "password",
            cart = Cart(emptyList()),
        )
}
