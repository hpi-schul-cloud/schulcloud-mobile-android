package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.drawer_navigation.*
import org.schulcloud.mobile.R

class NavigationDrawerFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.drawer_navigation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Handle logout

        val navController = findNavController(this)
        NavigationUI.setupWithNavController(navigationView, navController)
        var firstNavigation = true
        navController.addOnNavigatedListener { _, _ ->
            if (!firstNavigation)
                dismiss()
            firstNavigation = !firstNavigation
        }
    }
}
