package woowacourse.shopping.ui.coupon.uimodel

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val id: Long,
    val title: String,
    val expireDate: String,
    val minimumAmount: Int?,
    val availableStartTime: String?,
    val availableEndTime: String?,
    val isChecked: Boolean = false,
) {
    constructor(
        id: Long,
        title: String,
        expireDate: LocalDate,
        minimumAmount: Int?,
        availableStartTime: LocalTime?,
        availableEndTime: LocalTime?,
    ) : this(
        id,
        title,
        expireDate.format(dateFormatter),
        minimumAmount,
        availableStartTime?.format(timeFormatter),
        availableEndTime?.format(timeFormatter),
    )

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
