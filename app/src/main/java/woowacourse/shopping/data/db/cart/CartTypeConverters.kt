package woowacourse.shopping.data.db.cart

import androidx.room.TypeConverter
import woowacourse.shopping.domain.model.product.Quantity

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
