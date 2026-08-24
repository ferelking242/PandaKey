package juloo.keyboard2

import android.content.Context
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View

object VibratorCompat
{
  @JvmStatic
  fun vibrate(v: View, config: Config)
  {
    if (config.vibrate_custom)
    {
      if (config.vibrate_duration > 0)
        vibrator_vibrate(v, config.vibrate_duration)
    }
    else
    {
      v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP,
          HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING)
    }
  }

  /** Use the older [Vibrator] when the newer API is not available or the user
      wants more control. */
  private fun vibrator_vibrate(v: View, duration: Long)
  {
    try
    {
      get_vibrator(v).vibrate(duration)
    }
    catch (_: Exception) {}
  }

  private var vibrator_service: Vibrator? = null

  private fun get_vibrator(v: View): Vibrator
  {
    if (vibrator_service == null)
    {
      vibrator_service =
        v.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    return vibrator_service!!
  }
}
