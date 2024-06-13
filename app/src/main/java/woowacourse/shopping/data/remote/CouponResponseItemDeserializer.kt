package woowacourse.shopping.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import woowacourse.shopping.data.model.coupon.CouponResponseItem
import woowacourse.shopping.domain.model.Coupon.Companion.DISCOUNT_TYPE_BUYX_GETY
import woowacourse.shopping.domain.model.Coupon.Companion.DISCOUNT_TYPE_FIXED
import woowacourse.shopping.domain.model.Coupon.Companion.DISCOUNT_TYPE_FREE_SHIPPING
import woowacourse.shopping.domain.model.Coupon.Companion.DISCOUNT_TYPE_PERCENTAGE
import java.lang.reflect.Type

object CouponResponseItemDeserializer : JsonDeserializer<CouponResponseItem> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): CouponResponseItem {
        val jsonObject = json.asJsonObject
        return when (jsonObject.get("discountType").asString) {
            DISCOUNT_TYPE_FIXED -> context.deserialize(json, CouponResponseItem.Fixed::class.java)
            DISCOUNT_TYPE_BUYX_GETY -> context.deserialize(json, CouponResponseItem.BuyXGetY::class.java)
            DISCOUNT_TYPE_FREE_SHIPPING -> context.deserialize(json, CouponResponseItem.FreeShipping::class.java)
            DISCOUNT_TYPE_PERCENTAGE -> context.deserialize(json, CouponResponseItem.MiracleSale::class.java)
            else -> context.deserialize(json, CouponResponseItem.Etc::class.java)
        }
    }
}
