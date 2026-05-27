package woowacourse.shopping.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductTest {
    @Test
    fun `동일한 식별자를 가진 상품은 동일 상품이다`() {
        val product1 =
            Product(
                id = 1,
                name = ProductName("동원 스위트콘1"),
                price = Money(5000),
                imageUrl = "dsdsds",
                category = "book",
            )
        val product2 =
            Product(
                id = 1,
                name = ProductName("동원 스위트콘2"),
                price = Money(1000),
                imageUrl = "",
                category = "food",
            )
        assertThat(product1).isEqualTo(product2)
    }
}
