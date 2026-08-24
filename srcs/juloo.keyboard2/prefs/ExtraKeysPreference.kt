package juloo.keyboard2.prefs

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build.VERSION
import android.preference.CheckBoxPreference
import android.preference.PreferenceCategory
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import juloo.keyboard2.KeyValue
import juloo.keyboard2.KeyboardData
import juloo.keyboard2.R
import juloo.keyboard2.Theme
import java.util.HashMap

/** This class implements the "extra keys" preference but also defines the
    possible extra keys. */
class ExtraKeysPreference(context: Context, attrs: AttributeSet) :
    PreferenceCategory(context, attrs)
{
  /** Whether it has already been attached. */
  private var _attached = false

  init
  {
    setOrderingAsAdded(true)
  }

  override fun onAttachedToActivity()
  {
    if (_attached)
      return
    _attached = true
    for (key_name in extra_keys)
      addPreference(ExtraKeyCheckBoxPreference(getContext(), key_name,
            default_checked(key_name)))
  }

  class ExtraKeyCheckBoxPreference(ctx: Context, key_name: String,
      default_checked: Boolean) : CheckBoxPreference(ctx)
  {
    init
    {
      val kv = KeyValue.getKeyByName(key_name)
      var title = key_title(key_name, kv)
      val descr = key_description(ctx.getResources(), key_name)
      if (descr != null)
        title += " (" + descr + ")"
      setKey(pref_key_of_key_name(key_name))
      setDefaultValue(default_checked)
      setTitle(title)
      if (VERSION.SDK_INT >= 26)
        setSingleLineTitle(false)
    }

    override fun onBindView(view: View)
    {
      super.onBindView(view)
      val title = view.findViewById<View>(android.R.id.title) as TextView
      title.setTypeface(Theme.getKeyFont(getContext()))
    }
  }

  companion object
  {
    /** Array of the keys that can be selected. */
    val extra_keys = arrayOf(
      "alt",
      "meta",
      "compose",
      "voice_typing",
      "switch_clipboard",
      "accent_aigu",
      "accent_grave",
      "accent_double_aigu",
      "accent_dot_above",
      "accent_circonflexe",
      "accent_tilde",
      "accent_cedille",
      "accent_trema",
      "accent_ring",
      "accent_caron",
      "accent_macron",
      "accent_ogonek",
      "accent_breve",
      "accent_slash",
      "accent_bar",
      "accent_dot_below",
      "accent_hook_above",
      "accent_horn",
      "accent_double_grave",
      "accent_small_caps",
      "€",
      "ß",
      "£",
      "§",
      "†",
      "ª",
      "º",
      "zwj",
      "zwnj",
      "nbsp",
      "nnbsp",
      "tab",
      "esc",
      "page_up",
      "page_down",
      "home",
      "end",
      "switch_greekmath",
      "change_method",
      "capslock",
      "copy",
      "paste",
      "cut",
      "selectAll",
      "shareText",
      "pasteAsPlainText",
      "undo",
      "redo",
      "delete_word",
      "forward_delete_word",
      "superscript",
      "subscript",
      "f11_placeholder",
      "f12_placeholder",
      "menu",
      "scroll_lock",
      "combining_dot_above",
      "combining_double_aigu",
      "combining_slash",
      "combining_arrow_right",
      "combining_breve",
      "combining_bar",
      "combining_aigu",
      "combining_caron",
      "combining_cedille",
      "combining_circonflexe",
      "combining_grave",
      "combining_macron",
      "combining_ring",
      "combining_tilde",
      "combining_trema",
      "combining_ogonek",
      "combining_dot_below",
      "combining_horn",
      "combining_hook_above",
      "combining_vertical_tilde",
      "combining_inverted_breve",
      "combining_pokrytie",
      "combining_slavonic_psili",
      "combining_slavonic_dasia",
      "combining_payerok",
      "combining_titlo",
      "combining_vzmet",
      "combining_arabic_v",
      "combining_arabic_inverted_v",
      "combining_shaddah",
      "combining_sukun",
      "combining_fatha",
      "combining_dammah",
      "combining_kasra",
      "combining_hamza_above",
      "combining_hamza_below",
      "combining_alef_above",
      "combining_fathatan",
      "combining_kasratan",
      "combining_dammatan",
      "combining_alef_below",
      "combining_kavyka",
      "combining_palatalization"
    )

    /** Whether an extra key is enabled by default. */
    @JvmStatic
    fun default_checked(name: String): Boolean
    {
      when (name)
      {
        "voice_typing",
        "change_method",
        "switch_clipboard",
        "compose",
        "tab",
        "esc",
        "f11_placeholder",
        "f12_placeholder" -> return true
        else -> return false
      }
    }

    /** Text that describe a key. Might be null. */
    fun key_description(res: Resources, name: String): String?
    {
      var id = 0
      var additional_info: String? = null
      when (name)
      {
        "capslock" -> id = R.string.key_descr_capslock
        "change_method" -> id = R.string.key_descr_change_method
        "compose" -> id = R.string.key_descr_compose
        "copy" -> id = R.string.key_descr_copy
        "cut" -> id = R.string.key_descr_cut
        "end" -> {
          id = R.string.key_descr_end
          additional_info = format_key_combination(arrayOf("fn", "right"))
        }
        "home" -> {
          id = R.string.key_descr_home
          additional_info = format_key_combination(arrayOf("fn", "left"))
        }
        "page_down" -> {
          id = R.string.key_descr_page_down
          additional_info = format_key_combination(arrayOf("fn", "down"))
        }
        "page_up" -> {
          id = R.string.key_descr_page_up
          additional_info = format_key_combination(arrayOf("fn", "up"))
        }
        "paste" -> id = R.string.key_descr_paste
        "pasteAsPlainText" -> {
          id = R.string.key_descr_pasteAsPlainText
          additional_info = format_key_combination(arrayOf("fn", "paste"))
        }
        "redo" -> {
          id = R.string.key_descr_redo
          additional_info = format_key_combination(arrayOf("fn", "undo"))
        }
        "delete_word" ->
          additional_info = format_key_combination_gesture(res, "backspace")
        "forward_delete_word" ->
          additional_info = format_key_combination_gesture(res, "forward_delete")
        "selectAll" -> id = R.string.key_descr_selectAll
        "subscript" -> id = R.string.key_descr_subscript
        "superscript" -> id = R.string.key_descr_superscript
        "switch_greekmath" -> id = R.string.key_descr_switch_greekmath
        "undo" -> id = R.string.key_descr_undo
        "voice_typing" -> id = R.string.key_descr_voice_typing
        "ª" -> id = R.string.key_descr_ª
        "º" -> id = R.string.key_descr_º
        "switch_clipboard" -> id = R.string.key_descr_clipboard
        "zwj" -> id = R.string.key_descr_zwj
        "zwnj" -> id = R.string.key_descr_zwnj
        "nbsp" -> id = R.string.key_descr_nbsp
        "nnbsp" -> id = R.string.key_descr_nnbsp

        "accent_aigu",
        "accent_grave",
        "accent_double_aigu",
        "accent_dot_above",
        "accent_circonflexe",
        "accent_tilde",
        "accent_cedille",
        "accent_trema",
        "accent_ring",
        "accent_caron",
        "accent_macron",
        "accent_ogonek",
        "accent_breve",
        "accent_slash",
        "accent_bar",
        "accent_dot_below",
        "accent_hook_above",
        "accent_horn",
        "accent_double_grave",
        "accent_small_caps"
        -> id = R.string.key_descr_dead_key

        "combining_dot_above",
        "combining_double_aigu",
        "combining_slash",
        "combining_arrow_right",
        "combining_breve",
        "combining_bar",
        "combining_aigu",
        "combining_caron",
        "combining_cedille",
        "combining_circonflexe",
        "combining_grave",
        "combining_macron",
        "combining_ring",
        "combining_tilde",
        "combining_trema",
        "combining_ogonek",
        "combining_dot_below",
        "combining_horn",
        "combining_hook_above",
        "combining_vertical_tilde",
        "combining_inverted_breve",
        "combining_pokrytie",
        "combining_slavonic_psili",
        "combining_slavonic_dasia",
        "combining_payerok",
        "combining_titlo",
        "combining_vzmet",
        "combining_arabic_v",
        "combining_arabic_inverted_v",
        "combining_shaddah",
        "combining_sukun",
        "combining_fatha",
        "combining_dammah",
        "combining_kasra",
        "combining_hamza_above",
        "combining_hamza_below",
        "combining_alef_above",
        "combining_fathatan",
        "combining_kasratan",
        "combining_dammatan",
        "combining_alef_below",
        "combining_kavyka",
        "combining_palatalization"
        -> id = R.string.key_descr_combining
      }
      if (id == 0)
        return additional_info
      var descr = res.getString(id)
      if (additional_info != null)
        descr += "  —  " + additional_info
      return descr
    }

    fun key_title(key_name: String, kv: KeyValue): String
    {
      when (key_name)
      {
        "f11_placeholder" -> return "F11"
        "f12_placeholder" -> return "F12"
      }
      return kv.getString()
    }

    /** Format a key combination */
    fun format_key_combination(keys: Array<String>): String
    {
      val out = StringBuilder()
      for (i in keys.indices)
      {
        if (i > 0) out.append(" + ")
        out.append(KeyValue.getKeyByName(keys[i]).getString())
      }
      return out.toString()
    }

    /** Explain a gesture on a key */
    fun format_key_combination_gesture(res: Resources, key_name: String): String
    {
      return res.getString(R.string.key_descr_gesture) + " + " +
        KeyValue.getKeyByName(key_name).getString()
    }

    /** Place an extra key next to the key specified by the first argument, on
        bottom-right preferably or on the bottom-left. If the specified key is not
        on the layout, place on the specified row and column. */
    fun mk_preferred_pos(next_to_key: String?, row: Int, col: Int, prefer_bottom_right: Boolean): KeyboardData.PreferredPos
    {
      val next_to = if (next_to_key == null) null else KeyValue.getKeyByName(next_to_key)
      val d1: Int; val d2: Int // Preferred direction and fallback direction
      if (prefer_bottom_right) { d1 = 4; d2 = 3 } else { d1 = 3; d2 = 4 }
      return KeyboardData.PreferredPos(next_to,
              arrayOf(
                KeyboardData.KeyPos(row, col, d1),
                KeyboardData.KeyPos(row, col, d2),
                KeyboardData.KeyPos(row, -1, d1),
                KeyboardData.KeyPos(row, -1, d2),
                KeyboardData.KeyPos(-1, -1, -1)))
    }

    fun key_preferred_pos(key_name: String): KeyboardData.PreferredPos
    {
      when (key_name)
      {
        "cut" -> return mk_preferred_pos("x", 2, 2, true)
        "copy" -> return mk_preferred_pos("c", 2, 3, true)
        "paste" -> return mk_preferred_pos("v", 2, 4, true)
        "undo" -> return mk_preferred_pos("z", 2, 1, true)
        "selectAll" -> return mk_preferred_pos("a", 1, 0, true)
        "redo" -> return mk_preferred_pos("y", 0, 5, true)
        "f11_placeholder" -> return mk_preferred_pos("9", 0, 8, false)
        "f12_placeholder" -> return mk_preferred_pos("0", 0, 9, false)
        "delete_word" -> return mk_preferred_pos("backspace", -1, -1, false)
        "forward_delete_word" -> return mk_preferred_pos("backspace", -1, -1, true)
      }
      return KeyboardData.PreferredPos.DEFAULT
    }

    /** Get the set of enabled extra keys. */
    @JvmStatic
    fun get_extra_keys(prefs: SharedPreferences): Map<KeyValue, KeyboardData.PreferredPos>
    {
      val ks = HashMap<KeyValue, KeyboardData.PreferredPos>()
      for (key_name in extra_keys)
      {
        if (prefs.getBoolean(pref_key_of_key_name(key_name),
              default_checked(key_name)))
          ks[KeyValue.getKeyByName(key_name)] = key_preferred_pos(key_name)
      }
      return ks
    }

    @JvmStatic
    fun pref_key_of_key_name(key_name: String): String
    {
      return "extra_key_" + key_name
    }
  }
}
