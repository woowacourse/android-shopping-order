package woowacourse.shopping.data.database.client

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import woowacourse.shopping.data.model.dto.coupon.BuyXGetYCouponDto
import woowacourse.shopping.data.model.dto.coupon.CouponDto
import woowacourse.shopping.data.model.dto.coupon.FixedDiscountCouponDto
import woowacourse.shopping.data.model.dto.coupon.FreeShippingCouponDto
import woowacourse.shopping.data.model.dto.coupon.PercentageDiscountCouponDto
import java.lang.reflect.Type

class CouponDeserializer : JsonDeserializer<CouponDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): CouponDto {
        val jsonObject = json.asJsonObject
        return when (val discountType = jsonObject.get("discountType").asString) {
            DISCOUNT_TYPE_FIXED -> context.deserialize(jsonObject, FixedDiscountCouponDto::class.java)
            DISCOUNT_TYPE_BUY_X_GET_Y -> context.deserialize(jsonObject, BuyXGetYCouponDto::class.java)
            DISCOUNT_TYPE_FREE_SHIPPING -> context.deserialize(jsonObject, FreeShippingCouponDto::class.java)
            DISCOUNT_TYPE_PERCENTAGE -> context.deserialize(jsonObject, PercentageDiscountCouponDto::class.java)
            else -> throw JsonParseException("Unknown coupon type: $discountType")
        }
    }

    companion object {
        const val DISCOUNT_TYPE_FIXED = "fixed"
        const val DISCOUNT_TYPE_BUY_X_GET_Y = "buyXgetY"
        const val DISCOUNT_TYPE_FREE_SHIPPING = "freeShipping"
        const val DISCOUNT_TYPE_PERCENTAGE = "percentage"
    }
}
