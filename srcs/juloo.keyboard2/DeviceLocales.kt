package juloo.keyboard2

import android.content.Context
import android.os.Build.VERSION
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype

class DeviceLocales private constructor(
    @JvmField val installed: List<Loc>,
    @JvmField val default_: Loc?)
{
  /** Extra keys required by all the installed locales. */
  fun extra_keys(): ExtraKeys
  {
    val extra_keys = ArrayList<ExtraKeys>()
    for (l in installed)
      extra_keys.add(l.extra_keys)
    return ExtraKeys.merge(extra_keys)
  }

  class Loc(st: InputMethodSubtype)
  {
    @JvmField val lang_tag: String? = st.languageTag
    @JvmField val script: String? = st.getExtraValueOf("script")
    @JvmField val default_layout: String? = st.getExtraValueOf("default_layout")
    @JvmField val extra_keys: ExtraKeys
    @JvmField val dictionary: String? = st.getExtraValueOf("dictionary")

    init
    {
      val extra_keys_s = st.getExtraValueOf("extra_keys")
      extra_keys = if (extra_keys_s != null)
        ExtraKeys.parse(script, extra_keys_s)
      else
        ExtraKeys.EMPTY
    }
  }

  companion object
  {
    @JvmStatic
    fun load(ctx: Context): DeviceLocales
    {
      val imm =
        ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      val locs = get_installed_locales(ctx.getPackageName(), imm)
      return DeviceLocales(locs, current_locale(imm, locs))
    }

    private fun get_installed_locales(pkg: String, imm: InputMethodManager): List<Loc>
    {
      val locs = ArrayList<Loc>()
      for (imi in imm.getEnabledInputMethodList())
        if (imi.getPackageName() == pkg)
        {
          for (subtype in imm.getEnabledInputMethodSubtypeList(imi, true))
            locs.add(Loc(subtype))
          break
        }
      return locs
    }

    private fun current_locale(imm: InputMethodManager, installed: List<Loc>): Loc?
    {
      // Android might return a random subtype, for example, the first in the
      // list alphabetically.
      val current_subtype = imm.getCurrentInputMethodSubtype() ?: return null
      if (VERSION.SDK_INT < 24)
        return Loc(current_subtype)
      val default_lang_tag = current_subtype.getLanguageTag()
      for (l in installed)
        if (l.lang_tag == default_lang_tag)
          return l
      return null
    }
  }
}
