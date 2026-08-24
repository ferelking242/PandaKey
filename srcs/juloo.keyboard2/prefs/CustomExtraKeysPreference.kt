package juloo.keyboard2.prefs

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.TextView
import juloo.keyboard2.KeyValue
import juloo.keyboard2.KeyboardData
import juloo.keyboard2.R
import java.util.HashMap

/** Allows to enter custom keys to be added to the keyboard. This shows up at
    the top of the "Add keys to the keyboard" option. */
class CustomExtraKeysPreference(context: Context, attrs: AttributeSet) :
    ListGroupPreference<String>(context, attrs)
{
  init
  {
    setKey(KEY)
  }

  override fun label_of_value(value: String, i: Int): String = value

  override fun select(callback: SelectionCallback<String>, old_value: String?)
  {
    val content = View.inflate(getContext(), R.layout.dialog_edit_text, null)
    (content.findViewById<View>(R.id.text) as TextView).text = old_value
    AlertDialog.Builder(getContext())
      .setView(content)
      .setPositiveButton(android.R.string.ok) { dialog, _ ->
        val input = (dialog as AlertDialog).findViewById<EditText>(R.id.text)
        val k = input.text.toString()
        if (!k.equals(""))
          callback.select(k)
      }
      .setNegativeButton(android.R.string.cancel, null)
      .show()
  }

  override fun get_serializer(): Serializer<String> = SERIALIZER

  companion object
  {
    /** This pref stores a list of strings encoded as JSON. */
    private const val KEY = "custom_extra_keys"
    private val SERIALIZER: Serializer<String> = StringSerializer()

    @JvmStatic
    fun get(prefs: SharedPreferences): Map<KeyValue, KeyboardData.PreferredPos>
    {
      val kvs = HashMap<KeyValue, KeyboardData.PreferredPos>()
      val key_names = ListGroupPreference.load_from_preferences(KEY, prefs, null, SERIALIZER)
      if (key_names != null)
      {
        for (key_name in key_names)
          kvs[KeyValue.getKeyByName(key_name)] = KeyboardData.PreferredPos.DEFAULT
      }
      return kvs
    }
  }
}
