package juloo.keyboard2

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.core.util.Consumer

class FoldStateTracker(context: Context)
{
  private val _innerListener: Consumer<WindowLayoutInfo>
  private val _windowInfoTracker: WindowInfoTrackerCallbackAdapter
  private var _foldingFeature: FoldingFeature? = null
  private var _changedCallback: Runnable? = null

  init
  {
    _windowInfoTracker =
      WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(context))
    _innerListener = LayoutStateChangeCallback()
    _windowInfoTracker.addWindowLayoutInfoListener(context, Runnable::run, _innerListener)
  }

  fun isUnfolded(): Boolean
  {
    // FoldableFeature is only present when the device is unfolded. Otherwise, it's removed.
    // A weird decision from Google, but that's how it works:
    // https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:window/window/src/main/java/androidx/window/layout/adapter/sidecar/SidecarAdapter.kt;l=187?q=SidecarAdapter
    return _foldingFeature != null
  }

  fun close()
  {
    _windowInfoTracker.removeWindowLayoutInfoListener(_innerListener)
  }

  fun setChangedCallback(changedCallback: Runnable?)
  {
    _changedCallback = changedCallback
  }

  inner class LayoutStateChangeCallback : Consumer<WindowLayoutInfo>
  {
    override fun accept(newLayoutInfo: WindowLayoutInfo)
    {
      val old = _foldingFeature
      _foldingFeature = null
      val features: List<DisplayFeature> = newLayoutInfo.getDisplayFeatures()
      for (feature in features)
      {
        if (feature is FoldingFeature)
        {
          _foldingFeature = feature
        }
      }

      if (old !== _foldingFeature && _changedCallback != null)
      {
        _changedCallback!!.run()
      }
    }
  }

  companion object
  {
    @JvmStatic
    fun isFoldableDevice(context: Context): Boolean
    {
      if (VERSION.SDK_INT < 30)
        return false
      return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_HINGE_ANGLE)
    }
  }
}
