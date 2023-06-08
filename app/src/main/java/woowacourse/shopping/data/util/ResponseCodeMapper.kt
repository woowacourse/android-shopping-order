package woowacourse.shopping.data.util

import com.example.domain.model.FailureInfo

fun failureInfo(code: Int): FailureInfo = when (code) {
    401 -> FailureInfo.Unauthorized()
    404 -> FailureInfo.NotFound()
    504 -> FailureInfo.GatewayTimeout()
    else -> {
        if (code < 500) FailureInfo.ClientDefault()
        else FailureInfo.ServerDefault()
    }
}
