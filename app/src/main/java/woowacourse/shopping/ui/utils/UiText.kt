package woowacourse.shopping.ui.utils

import woowacourse.shopping.R
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError

fun ShowError.toUiText(): Int =
    when (this) {
        DataError.Network.REQUEST_TIMEOUT -> R.string.time_out_error
        DataError.Network.NO_INTERNET -> R.string.no_internet
        DataError.Network.SERVER -> R.string.server_error
        DataError.Network.INVALID_AUTHORIZATION -> R.string.unauthorized_error
        DataError.UNKNOWN -> R.string.unknown_error
    }
