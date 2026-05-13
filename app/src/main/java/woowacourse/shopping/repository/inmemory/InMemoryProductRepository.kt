package woowacourse.shopping.repository.inmemory

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Products
import woowacourse.shopping.repository.ProductRepository

object InMemoryProductRepository : ProductRepository {
    private const val PRODUCT_IMAGE_URL = "https://cdn.frame-less.co.kr/news/photo/202510/1344_3681_750.jpg"

    val DARAM =
        Product(
            id = 1L,
            name = "다람",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BBOYAMI =
        Product(
            id = 2L,
            name = "뽀야미",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BANILLA =
        Product(
            id = 3L,
            name = "바닐라",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val APPLE =
        Product(
            id = 4L,
            name = "애플",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val SYANTI =
        Product(
            id = 5L,
            name = "샤니",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val GLLUMIN =
        Product(
            id = 6L,
            name = "글루민",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val DARAM2 =
        Product(
            id = 7L,
            name = "다람2",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BBOYAMI2 =
        Product(
            id = 8L,
            name = "뽀야미2",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BANILLA2 =
        Product(
            id = 9L,
            name = "바닐라2",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val APPLE2 =
        Product(
            id = 10L,
            name = "애플2",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val SYANTI2 =
        Product(
            id = 11L,
            name = "샤니2",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val GLLUMIN2 =
        Product(
            id = 12L,
            name = "글루민2",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val DARAM3 =
        Product(
            id = 13L,
            name = "다람3",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BBOYAMI3 =
        Product(
            id = 14L,
            name = "뽀야미3",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BANILLA3 =
        Product(
            id = 15L,
            name = "바닐라3",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val APPLE3 =
        Product(
            id = 16L,
            name = "애플3",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val SYANTI3 =
        Product(
            id = 17L,
            name = "샤니3",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val GLLUMIN3 =
        Product(
            id = 18L,
            name = "글루민3",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val DARAM4 =
        Product(
            id = 19L,
            name = "다람4",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BBOYAMI4 =
        Product(
            id = 20L,
            name = "뽀야미4",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val BANILLA4 =
        Product(
            id = 21L,
            name = "바닐라4",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val APPLE4 =
        Product(
            id = 22L,
            name = "애플4",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val SYANTI4 =
        Product(
            id = 23L,
            name = "샤니4",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val GLLUMIN4 =
        Product(
            id = 24L,
            name = "글루민4",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
        )
    val products =
        Products(
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
            ),
        )
    override val size: Int
        get() = products.count()

    private val productMap: Map<Long, Product> = products.associateBy { it.id }

    override suspend fun getProducts(
        fromIndex: Int,
        limit: Int,
    ): Products {
        val safeFrom = fromIndex.coerceIn(0, products.count())
        val safeLimit = limit.coerceAtLeast(0)
        val safeTo = minOf(safeFrom + safeLimit, products.count())

        return Products(products.toList().subList(safeFrom, safeTo))
    }

    override suspend fun hasNext(current: Int) = current < products.toList().lastIndex

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        ids
            .mapNotNull { id ->
                productMap[id]?.let { id to it }
            }.toMap()
}
