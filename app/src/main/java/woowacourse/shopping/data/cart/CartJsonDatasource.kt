package woowacourse.shopping.data.cart

val jsonAllCartProducts = """
[
    {
        "id": 1,
        "quantity": 5,
        "product": {
            "id": 1,
            "price": 10000,
            "name": "치킨",
            "imageUrl": "http://example.com/chicken.jpg"
        }
    },
    {
        "id": 2,
        "quantity": 1,
        "product": {
            "id": 2,
            "price": 20000,
            "name": "피자",
            "imageUrl": "http://example.com/pizza.jpg"
        }
    }
]
""".trimIndent()

val jsonCartMap = mapOf<Long, String>(
    1L to """
        {
            "id": 1,
            "quantity": 5,
            "product": {
                "id": 1,
                "price": 10000,
                "name": "치킨",
                "imageUrl": "http://example.com/chicken.jpg"
            }
        }
    """,
    2L to """
        {
            "id": 2,
            "quantity": 1,
            "product": {
                "id": 2,
                "price": 20000,
                "name": "피자",
                "imageUrl": "http://example.com/pizza.jpg"
            }
        }
    """
)
