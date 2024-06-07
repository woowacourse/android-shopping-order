package woowacourse.shopping.ui.coupon

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityCouponBinding

class CouponActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCouponBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
