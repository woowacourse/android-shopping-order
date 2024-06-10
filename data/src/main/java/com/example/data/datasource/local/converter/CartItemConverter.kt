package com.example.data.datasource.local.converter

import androidx.room.TypeConverter
import com.example.domain.model.Quantity

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
