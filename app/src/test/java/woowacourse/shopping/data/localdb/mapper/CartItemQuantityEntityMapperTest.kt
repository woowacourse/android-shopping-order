package woowacourse.shopping.data.localdb.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class CartItemQuantityEntityMapperTest {
    @Test
    fun `도메인 장바구니 상품을 장바구니 엔티티로 변환한다`() {
        val cartItem =
            CartItem(
                id = 1,
                product =
                    Product(
                        id = 1,
                        name = ProductName("상품"),
                        price = Money(2000),
                        imageUrl = "1",
                        category = "book",
                    ),
                quantity = 3,
            )

        val entity = cartItem.toEntity()

        assertThat(entity.productId).isEqualTo(cartItem.product.id)
        assertThat(entity.quantity).isEqualTo(cartItem.quantity)
    }
}
