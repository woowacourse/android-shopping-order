package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiUserPointInfo = UserPointInfo

@Parcelize
class UserPointInfo(
    val point: Point,
    val earnRate: EarnRate
) : Parcelable
