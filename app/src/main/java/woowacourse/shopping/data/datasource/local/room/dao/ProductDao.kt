package woowacourse.shopping.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.datasource.local.room.entity.product.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE id = :id")
    fun findOrNull(id: Int): ProductEntity?

    @Query("SELECT * FROM products LIMIT :pageSize OFFSET :page * :pageSize")
    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<ProductEntity>

    @Query("SELECT COUNT(*) FROM products")
    fun totalCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(productEntities: List<ProductEntity>)
}
