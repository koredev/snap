package com.koredev.snap.ui.snap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.koredev.snap.AppExecutors
import com.koredev.snap.R
import com.koredev.snap.databinding.SnapFragmentBinding
import com.koredev.snap.di.Injectable
import com.koredev.snap.lib.FragmentDataBindingComponent
import com.koredev.snap.util.autoCleared
import com.koredev.snap.util.hideSystemUi
import com.koredev.snap.util.requestCameraPermissions
import kotlinx.android.synthetic.main.snap_fragment.*
import javax.inject.Inject

class SnapFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    lateinit var snapViewModel: SnapViewModel

    private var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<SnapFragmentBinding>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestCameraPermissions()
        hideSystemUi()
        snapViewModel = ViewModelProviders.of(this, viewModelFactory).get(SnapViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.snap_fragment,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }
}