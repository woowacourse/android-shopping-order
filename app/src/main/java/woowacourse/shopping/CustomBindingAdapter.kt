package woowacourse.shopping

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import woowacourse.shopping.model.OrderProductUIModel

object CustomBindingAdapter {

    @BindingAdapter("imgResId")
    @JvmStatic
    fun setImageResource(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("orderProductsName")
    @JvmStatic
    fun setOrderProductsName(view: TextView, orderProducts: List<OrderProductUIModel>?) {
        val orderProductsName = orderProducts?.joinToString(", ") { it.product.name }
        view.text = orderProductsName
    }
}
