package woowacourse.shopping.ui.fixture

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product

object MockProducts {
    private const val PRODUCT_IMAGE_URL = "https://cdn.frame-less.co.kr/news/photo/202510/1344_3681_750.jpg"

    val DARAM = product(id = 1L, name = "다람", price = 10_000, category = "dessert")
    val BBOYAMI = product(id = 2L, name = "뽀야미", price = 11_000, category = "dessert")
    val BANILLA = product(id = 3L, name = "바닐라", price = 12_000, category = "dessert")
    val APPLE = product(id = 4L, name = "애플", price = 13_000, category = "fruit")
    val SYANTI = product(id = 5L, name = "샤니", price = 14_000, category = "snack")
    val GLLUMIN = product(id = 6L, name = "글루민", price = 15_000, category = "snack")
    val DARAM2 = product(id = 7L, name = "다람2", price = 10_000, category = "dessert")
    val BBOYAMI2 = product(id = 8L, name = "뽀야미2", price = 11_000, category = "dessert")
    val BANILLA2 = product(id = 9L, name = "바닐라2", price = 12_000, category = "dessert")
    val APPLE2 = product(id = 10L, name = "애플2", price = 13_000, category = "fruit")
    val SYANTI2 = product(id = 11L, name = "샤니2", price = 14_000, category = "snack")
    val GLLUMIN2 = product(id = 12L, name = "글루민2", price = 15_000, category = "snack")
    val DARAM3 = product(id = 13L, name = "다람3", price = 10_000, category = "dessert")
    val BBOYAMI3 = product(id = 14L, name = "뽀야미3", price = 11_000, category = "dessert")
    val BANILLA3 = product(id = 15L, name = "바닐라3", price = 12_000, category = "dessert")
    val APPLE3 = product(id = 16L, name = "애플3", price = 13_000, category = "fruit")
    val SYANTI3 = product(id = 17L, name = "샤니3", price = 14_000, category = "snack")
    val GLLUMIN3 = product(id = 18L, name = "글루민3", price = 15_000, category = "snack")
    val DARAM4 = product(id = 19L, name = "다람4", price = 10_000, category = "dessert")
    val BBOYAMI4 = product(id = 20L, name = "뽀야미4", price = 11_000, category = "dessert")
    val BANILLA4 = product(id = 21L, name = "바닐라4", price = 12_000, category = "dessert")
    val APPLE4 = product(id = 22L, name = "애플4", price = 13_000, category = "fruit")
    val SYANTI4 = product(id = 23L, name = "샤니4", price = 14_000, category = "snack")
    val GLLUMIN4 = product(id = 24L, name = "글루민4", price = 15_000, category = "snack")

    val products =
        listOf(
            DARAM,
            BBOYAMI,
            BANILLA,
            APPLE,
            GLLUMIN,
            SYANTI,
            DARAM2,
            BBOYAMI2,
            BANILLA2,
            APPLE2,
            GLLUMIN2,
            SYANTI2,
            DARAM3,
            BBOYAMI3,
            BANILLA3,
            APPLE3,
            GLLUMIN3,
            SYANTI3,
            DARAM4,
            BBOYAMI4,
            BANILLA4,
            APPLE4,
            GLLUMIN4,
            SYANTI4,
        )

    private fun product(
        id: Long,
        name: String,
        price: Int,
        category: String,
    ): Product =
        Product(
            id = id,
            name = name,
            price = Money(price),
            imageUrl = PRODUCT_IMAGE_URL,
            category = category,
        )
}
