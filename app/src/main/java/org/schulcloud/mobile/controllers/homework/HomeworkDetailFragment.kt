package org.schulcloud.mobile.controllers.homework


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment

class HomeworkDetailFragment () : BaseFragment() {

    companion object {
        val TAG: String = HomeworkDetailFragment::class.java.simpleName
        const val ARG_HOMEWORK_ID = "ARG_HOMEWORK_ID"

        @JvmStatic
        fun getInstance(id: String): HomeworkDetailFragment{
            val homeworkDetailFragment = HomeworkDetailFragment()
            val args = Bundle()
            args.putString(ARG_HOMEWORK_ID, id)
            homeworkDetailFragment.arguments = args

            return homeworkDetailFragment
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_homework_detail, container, false)
    }


}
