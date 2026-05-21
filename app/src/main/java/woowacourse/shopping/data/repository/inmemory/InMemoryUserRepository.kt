package woowacourse.shopping.data.repository.inmemory

import woowacourse.shopping.data.model.Cart
import woowacourse.shopping.data.model.User

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
