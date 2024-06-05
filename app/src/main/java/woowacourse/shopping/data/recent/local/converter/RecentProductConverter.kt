package woowacourse.shopping.data.recent.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import woowacourse.shopping.data.recent.local.entity.ProductEntity
import java.time.LocalDateTime

class RecentProductConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertSeenDateTimeToString(seenDateTime: LocalDateTime): String {
        return seenDateTime.toString()
    }

    @TypeConverter
    fun convertStringToSeenDateTime(seenDateTimeValue: String): LocalDateTime {
        return LocalDateTime.parse(seenDateTimeValue)
    }

    @TypeConverter
    fun convertProductToString(product: ProductEntity): String {
        return gson.toJson(product)
    }

    @TypeConverter
    fun convertStringToProduct(productValue: String): ProductEntity {
        return gson.fromJson(productValue, ProductEntity::class.java)
    }
}
