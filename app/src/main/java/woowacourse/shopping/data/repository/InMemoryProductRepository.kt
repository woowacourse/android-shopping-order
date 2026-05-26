package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.model.common.Money
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.query.ProductPageResult

object InMemoryProductRepository : ProductRepository {
    private const val PRODUCT_IMAGE_URL = "https://cdn.frame-less.co.kr/news/photo/202510/1344_3681_750.jpg"

    val DARAM =
        Product(
            id = 1L,
            name = "다람",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(0),
        )
    val BBOYAMI =
        Product(
            id = 2L,
            name = "뽀야미",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(1),
        )
    val BANILLA =
        Product(
            id = 3L,
            name = "바닐라",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(2),
        )
    val APPLE =
        Product(
            id = 4L,
            name = "애플",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(3),
        )
    val SYANTI =
        Product(
            id = 5L,
            name = "샤니",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(4),
        )
    val GLLUMIN =
        Product(
            id = 6L,
            name = "글루민",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(5),
        )
    val DARAM2 =
        Product(
            id = 7L,
            name = "다람2",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(6),
        )
    val BBOYAMI2 =
        Product(
            id = 8L,
            name = "뽀야미2",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(7),
        )
    val BANILLA2 =
        Product(
            id = 9L,
            name = "바닐라2",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(8),
        )
    val APPLE2 =
        Product(
            id = 10L,
            name = "애플2",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(9),
        )
    val SYANTI2 =
        Product(
            id = 11L,
            name = "샤니2",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(10),
        )
    val GLLUMIN2 =
        Product(
            id = 12L,
            name = "글루민2",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(11),
        )
    val DARAM3 =
        Product(
            id = 13L,
            name = "다람3",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(12),
        )
    val BBOYAMI3 =
        Product(
            id = 14L,
            name = "뽀야미3",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(13),
        )
    val BANILLA3 =
        Product(
            id = 15L,
            name = "바닐라3",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(14),
        )
    val APPLE3 =
        Product(
            id = 16L,
            name = "애플3",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(15),
        )
    val SYANTI3 =
        Product(
            id = 17L,
            name = "샤니3",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(16),
        )
    val GLLUMIN3 =
        Product(
            id = 18L,
            name = "글루민3",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(17),
        )
    val DARAM4 =
        Product(
            id = 19L,
            name = "다람4",
            price = Money(10_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(18),
        )
    val BBOYAMI4 =
        Product(
            id = 20L,
            name = "뽀야미4",
            price = Money(11_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(19),
        )
    val BANILLA4 =
        Product(
            id = 21L,
            name = "바닐라4",
            price = Money(12_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(20),
        )
    val APPLE4 =
        Product(
            id = 22L,
            name = "애플4",
            price = Money(13_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(21),
        )
    val SYANTI4 =
        Product(
            id = 23L,
            name = "샤니4",
            price = Money(14_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(22),
        )
    val GLLUMIN4 =
        Product(
            id = 24L,
            name = "글루민4",
            price = Money(15_000),
            imageUrl = PRODUCT_IMAGE_URL,
            category = categoryOf(23),
        )
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

    private val productMap: Map<Long, Product> = products.associateBy(Product::id)

    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductPageResult = createPageResult(products, page, size)

    override suspend fun getProductsByCategory(
        category: String,
        page: Int,
        size: Int,
    ): ProductPageResult = createPageResult(products.filter { it.category == category }, page, size)

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        ids
            .mapNotNull { id ->
                productMap[id]?.let { id to it }
            }.toMap()

    private fun createPageResult(
        source: List<Product>,
        page: Int,
        size: Int,
    ): ProductPageResult {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(0)
        if (safeSize == 0) {
            return ProductPageResult(
                items = emptyList(),
                totalElements = source.size,
                page = safePage,
                size = safeSize,
                hasNext = false,
            )
        }

        val fromIndex = (safePage * safeSize).coerceIn(0, source.size)
        val toIndex = minOf(fromIndex + safeSize, source.size)

        return ProductPageResult(
            items = source.subList(fromIndex, toIndex),
            totalElements = source.size,
            page = safePage,
            size = safeSize,
            hasNext = toIndex < source.size,
        )
    }

    private fun categoryOf(index: Int): String =
        when (index % 3) {
            0 -> "dessert"
            1 -> "fruit"
            else -> "snack"
        }
}
