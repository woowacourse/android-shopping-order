package woowacourse.shopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outstanding_products")
data class OutstandingProductEntity(
    @PrimaryKey @ColumnInfo(name = "cartItemId") val cartItemId: Long,
)
