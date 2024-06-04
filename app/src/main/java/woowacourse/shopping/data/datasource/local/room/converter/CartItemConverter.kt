package woowacourse.shopping.data.datasource.local.room.converter

import androidx.room.TypeConverter
import woowacourse.shopping.domain.model.Quantity

class CartItemConverter {
    @TypeConverter
    fun convertQuantityToInt(quantity: Quantity): Int {
        return quantity.count
    }

    @TypeConverter
    fun convertIntToQuantity(quantityCount: Int): Quantity {
        return Quantity(quantityCount)
    }
}
