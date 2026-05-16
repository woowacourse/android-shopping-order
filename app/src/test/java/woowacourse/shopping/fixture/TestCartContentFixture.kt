package woowacourse.shopping.fixture

import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product
import woowacourse.shopping.fixture.TestCartContentFixture.cartContent
import woowacourse.shopping.fixture.TestProductFixture.product

object TestCartContentFixture {

    fun cartContent(
        product: Product = product(),
        quantity: Int = 1,
        id: String = product.id,
    ): CartContent = CartContent(
        product = product,
        quantity = quantity,
        id = id + 1000,
    )

    fun cartContentsOf(
        products: List<Product>,
        quantity: Int = 1,
    ): List<CartContent> = products.mapIndexed { index, p ->
        cartContent(
            product = p,
            quantity = quantity,
            id = (index + 1000).toString(),
        )
    }
}
