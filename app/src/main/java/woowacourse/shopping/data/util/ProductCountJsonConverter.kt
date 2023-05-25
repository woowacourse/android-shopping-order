package woowacourse.shopping.data.util

import org.json.JSONObject
import woowacourse.shopping.data.model.ProductCount

fun ProductCount.toJson(): String = JSONObject().apply {
    put("quantity", value)
}.toString()
