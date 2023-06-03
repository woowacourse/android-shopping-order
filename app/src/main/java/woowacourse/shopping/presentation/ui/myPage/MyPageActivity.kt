package woowacourse.shopping.presentation.ui.myPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity(), MyPageContract.View {
    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun showCharge() {
        TODO("Not yet implemented")
    }

    override fun requestRecharge() {
        TODO("Not yet implemented")
    }
}
