package juloo.keyboard2.prefs

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import juloo.keyboard2.CustomLayoutEditDialog
import juloo.keyboard2.KeyValue
import juloo.keyboard2.KeyboardData
import juloo.keyboard2.R
import juloo.keyboard2.Utils
import org.json.JSONException
import org.json.JSONObject

class LayoutsPreference(ctx: Context, attrs: AttributeSet) :
    ListGroupPreference<LayoutsPreference.Layout>(ctx, attrs)
{
  /** Text displayed for each layout in the dialog list. */
  private lateinit var _layout_display_names: Array<String>

  init
  {
    setKey(KEY)
    val res = ctx.getResources()
    _layout_display_names = res.getStringArray(R.array.pref_layout_entries)
  }

  override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?)
  {
    super.onSetInitialValue(restoreValue, defaultValue)
    if (_values.size == 0)
      set_values(ArrayList<Layout>(DEFAULT), false)
  }

  private fun label_of_layout(l: Layout): String
  {
    if (l is NamedLayout)
    {
      val lname = l.name
      val value_i = get_layout_names(getContext().getResources()).indexOf(lname)
      return if (value_i < 0) lname else _layout_display_names[value_i]
    }
    else if (l is CustomLayout)
    {
      // Use the layout's name if possible
      if (l.parsed != null && l.parsed!!.name != null
          && !l.parsed!!.name.equals(""))
        return l.parsed!!.name!!
      else
        return getContext().getString(R.string.pref_layout_e_custom)
    }
    else // instanceof SystemLayout
      return getContext().getString(R.string.pref_layout_e_system)
  }

  override fun label_of_value(value: Layout, i: Int): String
  {
    return getContext().getString(R.string.pref_layouts_item, i + 1,
        label_of_layout(value))
  }

  override fun on_attach_add_button(prev_btn: AddButton?): AddButton
  {
    if (prev_btn == null)
      return LayoutsAddButton(getContext())
    return prev_btn
  }

  override fun should_allow_remove_item(value: Layout): Boolean
  {
    return (_values.size > 1 && value !is CustomLayout)
  }

  override fun get_serializer(): ListGroupPreference.Serializer<Layout> = SERIALIZER

  private fun select_dialog(callback: SelectionCallback<Layout>)
  {
    val layouts = ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, _layout_display_names)
    AlertDialog.Builder(getContext())
      .setView(View.inflate(getContext(), R.layout.dialog_edit_text, null))
      .setAdapter(layouts) { _dialog, which ->
        val name = get_layout_names(getContext().getResources()).get(which)
        when (name)
        {
          "system" -> callback.select(SystemLayout())
          "custom" -> select_custom(callback, read_initial_custom_layout())
          else -> callback.select(NamedLayout(name))
        }
      }
      .show()
  }

  /** Dialog for specifying a custom layout. [initial_text] is the layout
      description when modifying a layout. */
  private fun select_custom(callback: SelectionCallback<Layout>, initial_text: String)
  {
    val allow_remove = callback.allow_remove() && _values.size > 1
    CustomLayoutEditDialog.show(getContext(), initial_text, allow_remove,
        object : CustomLayoutEditDialog.Callback
        {
          override fun select(text: String?)
          {
            if (text == null)
              callback.select(null)
            else
              callback.select(CustomLayout.parse(text))
          }

          override fun validate(text: String): String?
          {
            return try
            {
              KeyboardData.load_string_exn(text)
              null // Validation passed
            }
            catch (e: Exception)
            {
              e.message
            }
          }
        })
  }

  /** Called when modifying a layout. Custom layouts behave differently. */
  override fun select(callback: SelectionCallback<Layout>, prev_layout: Layout?)
  {
    if (prev_layout is CustomLayout)
      select_custom(callback, prev_layout.xml)
    else
      select_dialog(callback)
  }

  /** The initial text for the custom layout entry box. The qwerty_us layout is
      a good default and contains a bit of documentation. */
  private fun read_initial_custom_layout(): String
  {
    return try
    {
      val res = getContext().getResources()
      Utils.read_all_utf8(res.openRawResource(R.raw.latn_qwerty_us))
    }
    catch (_: Exception)
    {
      ""
    }
  }

  inner class LayoutsAddButton(ctx: Context) : AddButton(ctx)
  {
    init
    {
      setLayoutResource(R.layout.pref_layouts_add_btn)
    }
  }

  /** A layout selected by the user. The only implementations are
      [NamedLayout], [SystemLayout] and [CustomLayout]. */
  interface Layout

  class SystemLayout : Layout

  /** The name of a layout defined in [srcs/layouts]. */
  class NamedLayout(val name: String) : Layout

  /** The XML description of a custom layout. */
  class CustomLayout(val xml: String, val parsed: KeyboardData?) : Layout
  {
    companion object
    {
      @JvmStatic
      fun parse(xml: String): CustomLayout
      {
        val parsed: KeyboardData? = try { KeyboardData.load_string_exn(xml) } catch (_: Exception) { null }
        return CustomLayout(xml, parsed)
      }
    }
  }

  /** Named layouts are serialized to strings and custom layouts to JSON
      objects with a [kind] field. */
  class Serializer : ListGroupPreference.Serializer<Layout>
  {
    @Throws(JSONException::class)
    override fun load_item(obj: Any): Layout
    {
      if (obj is String)
      {
        if (obj == "system")
          return SystemLayout()
        return NamedLayout(obj)
      }
      val obj_ = obj as JSONObject
      when (obj_.getString("kind"))
      {
        "custom" -> return CustomLayout.parse(obj_.getString("xml"))
        else -> return SystemLayout()
      }
    }

    @Throws(JSONException::class)
    override fun save_item(v: Layout): Any
    {
      if (v is NamedLayout)
        return v.name
      if (v is CustomLayout)
        return JSONObject().put("kind", "custom")
          .put("xml", v.xml)
      return JSONObject().put("kind", "system")
    }
  }

  companion object
  {
    private const val KEY = "layouts"
    private val DEFAULT: List<Layout> = listOf(SystemLayout())
    private val SERIALIZER: ListGroupPreference.Serializer<Layout> = Serializer()

    /** Obtained from [res/values/layouts.xml]. */
    private var _unsafe_layout_ids_str: List<String>? = null
    private var _unsafe_layout_ids_res: TypedArray? = null

    /** Layout internal names. Contains "system" and "custom". */
    @JvmStatic
    fun get_layout_names(res: Resources): List<String>
    {
      if (_unsafe_layout_ids_str == null)
        _unsafe_layout_ids_str = res.getStringArray(R.array.pref_layout_values).toList()
      return _unsafe_layout_ids_str!!
    }

    /** Layout resource id for a layout name. [-1] if not found. */
    @JvmStatic
    fun layout_id_of_name(res: Resources, name: String): Int
    {
      if (_unsafe_layout_ids_res == null)
        _unsafe_layout_ids_res = res.obtainTypedArray(R.array.layout_ids)
      val i = get_layout_names(res).indexOf(name)
      if (i >= 0)
        return _unsafe_layout_ids_res!!.getResourceId(i, 0)
      return -1
    }

    /** [null] for the "system" layout. */
    @JvmStatic
    fun load_from_preferences(res: Resources, prefs: SharedPreferences): List<KeyboardData?>
    {
      val layouts = ArrayList<KeyboardData?>()
      for (l in ListGroupPreference.load_from_preferences(KEY, prefs, DEFAULT, SERIALIZER)!!)
      {
        when (l)
        {
          is NamedLayout -> layouts.add(layout_of_string(res, l.name))
          is CustomLayout -> layouts.add(l.parsed)
          else -> layouts.add(null) // SystemLayout
        }
      }
      return layouts
    }

    /** Does not call [prefs.commit()]. */
    @JvmStatic
    fun save_to_preferences(prefs: SharedPreferences.Editor, items: List<Layout>)
    {
      ListGroupPreference.save_to_preferences(KEY, prefs, items, SERIALIZER)
    }

    @JvmStatic
    fun layout_of_string(res: Resources, name: String): KeyboardData?
    {
      val id = layout_id_of_name(res, name)
      if (id > 0)
        return KeyboardData.load(res, id)
      // Might happen when the app is downgraded, return the system layout.
      return null
    }
  }
}
