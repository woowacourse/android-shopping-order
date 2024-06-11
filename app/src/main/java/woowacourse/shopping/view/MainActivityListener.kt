package woowacourse.shopping.view

import androidx.fragment.app.Fragment

interface MainActivityListener {
    fun changeFragment(nextFragment: Fragment)

    fun popFragment()

    fun resetFragment()
}
