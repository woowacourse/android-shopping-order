package woowacourse.shopping.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@BindingAdapter(value = ["formatDate", "formatPattern"])
fun setDateFormat(
    view: TextView,
    date: LocalDate?,
    pattern: String?,
) {
    if (date != null && !pattern.isNullOrBlank()) {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        view.text = date.format(formatter)
    } else {
        view.text = ""
    }
}

@BindingAdapter(value = ["formatTime", "formatPattern"])
fun setTimeFormat(
    view: TextView,
    time: LocalTime?,
    pattern: String?,
) {
    if (time != null && !pattern.isNullOrBlank()) {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        view.text = time.format(formatter)
    } else {
        view.text = ""
    }
}
