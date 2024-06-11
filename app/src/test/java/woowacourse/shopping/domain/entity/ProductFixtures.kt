package woowacourse.shopping.domain.entity

fun fakeProduct(
    id: Long = 1,
    price: Int = 1000,
    name: String = "콜라",
    imageUrl: String = "https://image.com",
    category: String = "음료",
): Product {
    return Product(
        id = id,
        price = price,
        name = name,
        imageUrl = imageUrl,
        category = category,
    )
}
