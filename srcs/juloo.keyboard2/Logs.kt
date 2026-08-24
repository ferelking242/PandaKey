package juloo.keyboard2

import android.text.InputType
import android.util.Log
import android.util.LogPrinter
import android.view.inputmethod.EditorInfo

object Logs
{
  private const val TAG = "juloo.keyboard2"

  private var _debug_logs: LogPrinter? = null

  @JvmStatic
  fun set_debug_logs(d: Boolean)
  {
    _debug_logs = if (d) LogPrinter(Log.DEBUG, TAG) else null
  }

  @JvmStatic
  fun debug_startup_input_view(info: EditorInfo, conf: Config)
  {
    val printer = _debug_logs ?: return
    info.dump(printer, "")
    if (info.extras != null)
      printer.println("extras: " + info.extras.toString())
    printer.println("class: " + (info.inputType and InputType.TYPE_MASK_CLASS))
    printer.println("flags: " + (info.inputType and InputType.TYPE_MASK_FLAGS))
    printer.println("variation: " + (info.inputType and InputType.TYPE_MASK_VARIATION))
  }

  @JvmStatic
  fun debug_config_migration(from_version: Int, to_version: Int)
  {
    debug("Migrating config version from " + from_version + " to " + to_version)
  }

  @JvmStatic
  fun debug(s: String)
  {
    _debug_logs?.println(s)
  }

  @JvmStatic
  fun exn(msg: String, e: Exception)
  {
    Log.e(TAG, msg, e)
  }

  @JvmStatic
  fun trace()
  {
    _debug_logs?.println(Log.getStackTraceString(Exception()))
  }
}
