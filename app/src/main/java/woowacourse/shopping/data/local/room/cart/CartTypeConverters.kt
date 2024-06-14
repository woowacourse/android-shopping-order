package woowacourse.shopping.data.local.room.cart

import androidx.room.TypeConverter
import woowacourse.shopping.domain.model.Quantity

class CartTypeConverters {
    @TypeConverter
    fun fromQuantity(quantity: Quantity): Int {
        return quantity.value
    }

    @TypeConverter
    fun toQuantity(value: Int): Quantity {
        return Quantity(value)
    }
}
