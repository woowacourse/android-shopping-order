package woowacourse.shopping.fixture

import android.R.attr.category
import android.util.Log.i
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

object TestProductFixture {

    fun product(
        id: String = "1",
        name: String = "품목 $id",
        price: Int = 1_000,
        imageUrl: String = "https://image/$id.png",
        category: String = "음료",
    ): Product = Product(
        name = name,
        price = Money(price),
        imageUrl = imageUrl,
        id = id,
        category = category,
    )

    fun products(
        count: Int,
        category: String = "음료",
        startIndex: Int = 1,
    ): List<Product> = (startIndex..(startIndex - 1) + count).map { i ->
        product(
            id = i.toString(),
            name = "품목$i",
            price = i * 1_000,
            category = category,
        )
    }
}
