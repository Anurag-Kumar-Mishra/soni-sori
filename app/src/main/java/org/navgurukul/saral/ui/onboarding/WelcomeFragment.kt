package org.navgurukul.saral.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.FragmentWelcomeBinding
import org.navgurukul.saral.util.AppUtils


class WelcomeFragment : Fragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
        const val TAG = "WelcomeFragment"
    }

    private lateinit var mBinding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.tvAlready.setOnClickListener {
            AppUtils.changeFragment(
                getParentFragmentManager(),
                LoginFragment.newInstance(),
                R.id.fragment_container_onboarding,
                true,
                LoginFragment.TAG
            )
        }
    }

}