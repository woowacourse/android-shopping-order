package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Query
import woowacourse.shopping.data.model.response.ProductResponse

@Dao
interface ProductDao {
    @Query(
        """
    SELECT 
        p.id, p.name, p.imageUrl, p.price,
        IFNULL(c.quantity, 0) AS cartQuantity
    FROM products p
    LEFT JOIN cart_products c ON p.id = c.productId
    WHERE p.id > :lastId
    ORDER BY p.id ASC
    LIMIT :count
    """,
    )
    fun getNextProducts(
        lastId: Int,
        count: Int,
    ): List<ProductResponse>

    @Query("SELECT MAX(id) FROM products")
    fun getMaxId(): Int

    @Query(
        """
    SELECT
        p.id, p.name, p.imageUrl, p.price,
        IFNULL(c.quantity, 0) AS cartQuantity
    FROM products p
    LEFT JOIN cart_products c ON p.id = c.productId
    WHERE p.id = :productId
    """,
    )
    fun getProduct(productId: Int): ProductResponse?

    @Query(
        """
    SELECT
        p.id, p.name, p.imageUrl, p.price,
        IFNULL(c.quantity, 0) AS cartQuantity
    FROM products p
    LEFT JOIN cart_products c ON p.id = c.productId
    WHERE p.id IN (:productIds)
    """,
    )
    fun getProducts(productIds: List<Int>): List<ProductResponse>
}
