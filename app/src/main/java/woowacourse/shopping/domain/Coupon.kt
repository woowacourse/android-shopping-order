package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
) : Parcelable
