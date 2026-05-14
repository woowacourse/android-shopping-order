package woowacourse.shopping.data.repository.inmemory

import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.Products
import woowacourse.shopping.data.repository.ProductRepository

class InMemoryProductRepository(
    initialProducts: List<Product> = emptyList(),
) : ProductRepository {
    val daram =
        Product(
            id = 1L,
            name = "다람",
            price = Money(10_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val bboyami =
        Product(
            id = 2L,
            name = "뽀야미",
            price = Money(11_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val banilla =
        Product(
            id = 3L,
            name = "바닐라",
            price = Money(12_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val apple =
        Product(
            id = 4L,
            name = "애플",
            price = Money(13_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val syanti =
        Product(
            id = 5L,
            name = "샤니",
            price = Money(14_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val gllumin =
        Product(
            id = 6L,
            name = "글루민",
            price = Money(15_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val daram2 =
        Product(
            id = 7L,
            name = "다람2",
            price = Money(10_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val bboyami2 =
        Product(
            id = 8L,
            name = "뽀야미2",
            price = Money(11_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val banilla2 =
        Product(
            id = 9L,
            name = "바닐라2",
            price = Money(12_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val apple2 =
        Product(
            id = 10L,
            name = "애플2",
            price = Money(13_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val syanti2 =
        Product(
            id = 11L,
            name = "샤니2",
            price = Money(14_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val gllumin2 =
        Product(
            id = 12L,
            name = "글루민2",
            price = Money(15_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val daram3 =
        Product(
            id = 13L,
            name = "다람3",
            price = Money(10_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val bboyami3 =
        Product(
            id = 14L,
            name = "뽀야미3",
            price = Money(11_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val banilla3 =
        Product(
            id = 15L,
            name = "바닐라3",
            price = Money(12_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val apple3 =
        Product(
            id = 16L,
            name = "애플3",
            price = Money(13_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val syanti3 =
        Product(
            id = 17L,
            name = "샤니3",
            price = Money(14_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val gllumin3 =
        Product(
            id = 18L,
            name = "글루민3",
            price = Money(15_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val daram4 =
        Product(
            id = 19L,
            name = "다람4",
            price = Money(10_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val bboyami4 =
        Product(
            id = 20L,
            name = "뽀야미4",
            price = Money(11_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val banilla4 =
        Product(
            id = 21L,
            name = "바닐라4",
            price = Money(12_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val apple4 =
        Product(
            id = 22L,
            name = "애플4",
            price = Money(13_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val syanti4 =
        Product(
            id = 23L,
            name = "샤니4",
            price = Money(14_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val gllumin4 =
        Product(
            id = 24L,
            name = "글루민4",
            price = Money(15_000),
            imageUrl = "https://picsum.photos/360/360",
        )
    val products: Products =
        if (initialProducts.isEmpty()) {
            Products(
                listOf(
                    daram,
                    bboyami,
                    banilla,
                    apple,
                    gllumin,
                    syanti,
                    daram2,
                    bboyami2,
                    banilla2,
                    apple2,
                    gllumin2,
                    syanti2,
                    daram3,
                    bboyami3,
                    banilla3,
                    apple3,
                    gllumin3,
                    syanti3,
                    daram4,
                    bboyami4,
                    banilla4,
                    apple4,
                    gllumin4,
                    syanti4,
                ),
            )
        } else {
            Products(initialProducts)
        }

    private val size = products.count()

    override suspend fun getSize(): Int = size

    override suspend fun getProducts(
        fromIndex: Int,
        count: Int,
    ): List<Product> = products.getPagedProducts(fromIndex, count)

    override suspend fun hasNext(currentIndex: Int): Boolean = currentIndex < size - 1

    override suspend fun findProduct(id: Long): Product? = products.firstOrNull { it.id == id }
}
