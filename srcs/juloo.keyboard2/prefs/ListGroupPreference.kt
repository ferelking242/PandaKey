package juloo.keyboard2.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceGroup
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import juloo.keyboard2.Logs
import juloo.keyboard2.R
import org.json.JSONArray
import org.json.JSONException

/** A list of preferences where the users can add items to the end and modify
    and remove items. Backed by a string list. Implement user selection in
    [select()]. */
abstract class ListGroupPreference<E> : PreferenceGroup
{
  private var _attached = false
  protected var _values: MutableList<E> = ArrayList()
  /** The "add" button currently displayed. */
  private var _add_button: AddButton? = null

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  {
    setOrderingAsAdded(true)
    setLayoutResource(R.layout.pref_listgroup_group)
  }

  /** Overrideable */

  /** The label to display on the item for a given value. */
  protected abstract fun label_of_value(value: E, i: Int): String

  /** Called every time the list changes and allows to change the "Add" button
      appearance.
      [prev_btn] is the previously attached button, might be null. */
  protected open fun on_attach_add_button(prev_btn: AddButton?): AddButton
  {
    if (prev_btn == null)
      return AddButton(getContext())
    return prev_btn
  }

  /** Called every time the list changes and allows to disable the "Remove"
      buttons on every items. Might be used to enforce a minimum number of
      items. */
  protected open fun should_allow_remove_item(value: E): Boolean
  {
    return true
  }

  /** Called when an item is added or modified. [old_value] is [null] if the
      item is being added. */
  protected abstract fun select(callback: SelectionCallback<E>, old_value: E?)

  /** A separate class is used as the same serializer must be used in the
      static context. See [Serializer] below. */
  protected abstract fun get_serializer(): Serializer<E>

  /** Load/save utils */

  companion object
  {
    /** Read a value saved by preference from a [SharedPreferences] object.
        [serializer] must be the same that is returned by [get_serializer()].
        Returns [null] on error. */
    @JvmStatic
    fun <E> load_from_preferences(key: String,
        prefs: SharedPreferences, def: List<E>?, serializer: Serializer<E>): List<E>?
    {
      val s = prefs.getString(key, null)
      return if (s != null) load_from_string(s, serializer) else def
    }

    /** Save items into the preferences. Does not call [prefs.commit()]. */
    @JvmStatic
    fun <E> save_to_preferences(key: String, prefs: SharedPreferences.Editor, items: List<E>, serializer: Serializer<E>)
    {
      prefs.putString(key, save_to_string(items, serializer))
    }

    /** Decode a list of string previously encoded with [save_to_string]. Returns
        [null] on error. */
    @JvmStatic
    fun <E> load_from_string(inp: String, serializer: Serializer<E>): List<E>?
    {
      return try
      {
        val l = ArrayList<E>()
        val arr = JSONArray(inp)
        for (i in 0 until arr.length())
          l.add(serializer.load_item(arr.get(i)))
        l
      }
      catch (e: JSONException)
      {
        Logs.exn("load_from_string", e)
        null
      }
    }

    /** Encode a list of string so it can be passed to
        [Preference.persistString()]. Decode with [load_from_string]. */
    @JvmStatic
    fun <E> save_to_string(items: List<E>, serializer: Serializer<E>): String
    {
      val serialized_items = ArrayList<Any>()
      for (it in items)
      {
        try
        {
          serialized_items.add(serializer.save_item(it))
        }
        catch (e: JSONException)
        {
          Logs.exn("save_to_string", e)
        }
      }
      return JSONArray(serialized_items).toString()
    }
  }

  /** Protected API */

  /** Set the values. If [persist] is [true], persist into the store. */
  protected fun set_values(vs: List<E>, persist: Boolean)
  {
    _values = vs.toMutableList()
    reattach()
    if (persist)
      persistString(save_to_string(vs, get_serializer()))
  }

  protected fun add_item(v: E)
  {
    _values.add(v)
    set_values(_values, true)
  }

  protected fun change_item(i: Int, v: E)
  {
    _values.set(i, v)
    set_values(_values, true)
  }

  protected fun remove_item(i: Int)
  {
    _values.removeAt(i)
    set_values(_values, true)
  }

  /** Internal */

  override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?)
  {
    val input = if (restoreValue) getPersistedString(null) else defaultValue as String?
    if (input != null)
    {
      val values = load_from_string(input, get_serializer())
      if (values != null)
        set_values(values, false)
    }
  }

  override fun onAttachedToActivity()
  {
    super.onAttachedToActivity()
    if (_attached)
      return
    _attached = true
    reattach()
  }

  private fun reattach()
  {
    if (!_attached)
      return
    removeAll()
    var i = 0
    for (v in _values)
    {
      addPreference(Item(getContext(), i, v))
      i++
    }
    _add_button = on_attach_add_button(_add_button)
    _add_button!!.setOrder(Preference.DEFAULT_ORDER)
    addPreference(_add_button)
  }

  inner class Item(ctx: Context, index: Int, value: E) : Preference(ctx)
  {
    val _value: E = value
    val _index: Int = index

    init
    {
      setPersistent(false)
      setTitle(label_of_value(value, index))
      if (should_allow_remove_item(value))
        setWidgetLayoutResource(R.layout.pref_listgroup_item_widget)
    }

    override fun onCreateView(parent: ViewGroup): View
    {
      val v = super.onCreateView(parent)
      val remove_btn = v.findViewById<View>(R.id.pref_listgroup_remove_btn)
      if (remove_btn != null)
        remove_btn.setOnClickListener { remove_item(_index) }
      v.setOnClickListener {
        select(object : SelectionCallback<E> {
          override fun select(value: E?)
          {
            if (value == null)
              remove_item(_index)
            else
              change_item(_index, value)
          }

          override fun allow_remove(): Boolean = true
        }, _value)
      }
      return v
    }
  }

  inner class AddButton(ctx: Context) : Preference(ctx)
  {
    init
    {
      setPersistent(false)
      setLayoutResource(R.layout.pref_listgroup_add_btn)
    }

    override fun onClick()
    {
      select(object : SelectionCallback<E> {
        override fun select(value: E?)
        {
          add_item(value!!)
        }

        override fun allow_remove(): Boolean = false
      }, null)
    }
  }

  interface SelectionCallback<E>
  {
    fun select(value: E?)

    /** If this method returns [true], [null] might be passed to [select] to
        remove the item. */
    fun allow_remove(): Boolean
  }

  /** Methods for serializing and deserializing abstract items.
      [StringSerializer] is an implementation. */
  interface Serializer<E>
  {
    /** [obj] is an object returned by [save_item()]. */
    @Throws(JSONException::class)
    fun load_item(obj: Any): E

    /** Serialize an item into JSON. Might return an object that can be inserted
        in a [JSONArray]. */
    @Throws(JSONException::class)
    fun save_item(v: E): Any
  }

  class StringSerializer : Serializer<String>
  {
    override fun load_item(obj: Any): String = obj as String
    override fun save_item(v: String): Any = v
  }
}
