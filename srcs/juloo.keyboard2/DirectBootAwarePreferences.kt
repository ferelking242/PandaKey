package juloo.keyboard2

import android.content.Context
import android.content.SharedPreferences
import android.os.Build.VERSION
import android.preference.PreferenceManager

object DirectBootAwarePreferences
{
  /* On API >= 24, preferences are read from the device protected storage. This
   * storage is less protected than the default, no personnal or sensitive
   * information is stored there (only the keyboard settings). This storage is
   * accessible during boot and allow the keyboard to read its settings and
   * allow typing the storage password. */
  @JvmStatic
  fun get_shared_preferences(context: Context): SharedPreferences
  {
    if (VERSION.SDK_INT < 24)
      return PreferenceManager.getDefaultSharedPreferences(context)
    val prefs = get_protected_prefs(context)
    check_need_migration(context, prefs)
    return prefs
  }

  /* Copy shared preferences to device protected storage. Not using
   * [Context.moveSharedPreferencesFrom] because the settings activity still
   * use [PreferenceActivity], which can't work on a non-default shared
   * preference file. */
  @JvmStatic
  fun copy_preferences_to_protected_storage(context: Context,
      src: SharedPreferences)
  {
    if (VERSION.SDK_INT >= 24)
      copy_shared_preferences(src, get_protected_prefs(context))
  }

  /** Load the default preferences. */
  @JvmStatic
  fun get_protected_prefs(context: Context): SharedPreferences
  {
    val pref_name =
      PreferenceManager.getDefaultSharedPreferencesName(context)
    return get_protected_prefs(context, pref_name)
  }

  /** Load the specified preferences. */
  @JvmStatic
  fun get_protected_prefs(context: Context, pref_name: String): SharedPreferences
  {
    var context = context
    if (VERSION.SDK_INT >= 24)
      context = context.createDeviceProtectedStorageContext()
    return context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
  }

  private fun check_need_migration(app_context: Context,
      protected_prefs: SharedPreferences)
  {
    if (!protected_prefs.getBoolean("need_migration", true))
      return
    val prefs: SharedPreferences
    try
    {
      prefs = PreferenceManager.getDefaultSharedPreferences(app_context)
    }
    catch (_: Exception)
    {
      // Device is locked, migrate later.
      return
    }
    prefs.edit().putBoolean("need_migration", false).apply()
    copy_shared_preferences(prefs, protected_prefs)
  }

  private fun copy_shared_preferences(src: SharedPreferences, dst: SharedPreferences)
  {
    val e = dst.edit()
    val entries = src.all
    for ((k, v) in entries)
    {
      when (v)
      {
        is Boolean -> e.putBoolean(k, v)
        is Float -> e.putFloat(k, v)
        is Int -> e.putInt(k, v)
        is Long -> e.putLong(k, v)
        is String -> e.putString(k, v)
        is Set<*> -> @Suppress("UNCHECKED_CAST") e.putStringSet(k, v as Set<String>)
      }
    }
    e.apply()
  }
}
