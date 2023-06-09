//      problem1: how to add the new die and it sort to the list. (optional)
//      problem2: how to get the die roll and show the result.
//      problem3: the display should be clear before showing the new result.
//      problem4: the result should show on the history result.
//      problem5: the history should be clear after clicking the button
// problem6: data can be saved, (optional)
// problem7: menu & setting to save or not (optional)
// problem8: reset die (optional)
//      problem 9: if side is not already in the array, added it, else, create alert dialogue.
// problem10: change to night theme
//      problem11: translation
//      problem12: save sipnner to pref
package pk.patarida.rollthedie

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.graphics.Paint
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
//import androidx.preference.PreferenceManager
import pk.patarida.rollthedie.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<Int>
    private lateinit var tvHist: TextView
    private lateinit var tvResult1: TextView
    private lateinit var tvResult2: TextView
    private lateinit var tvResult3: TextView
    private lateinit var die: Die
    private lateinit var sideArray: MutableList<Int>
    private lateinit var spinner: Spinner
    // add sharedPreference
    private lateinit var sharedPref: SharedPreferences
    private lateinit var allResult: String

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreference
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //PreferenceManager.getDefaultSharedPreferences(this)


        // init textView
        tvResult1 = binding.tvResult1
        tvResult2 = binding.tvResult2
        tvResult3 = binding.tvResult3
        tvHist = binding.tvHistoryResult

        allResult = ""

        // Create an array adapter for spinner // assign Basic sides: 4, 6, 8, 10, 12, 20
        sideArray = mutableListOf(4, 6, 8, 10, 12, 20)
        adapter = ArrayAdapter<Int>(this, android.R.layout.simple_dropdown_item_1line, sideArray )

        // Add array to spinner
        spinner = binding.spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(applicationContext, "${spinner.selectedItem} is selected",Toast.LENGTH_SHORT).show()
                die = Die(spinner.selectedItem.toString().toInt())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        // add new die
        binding.buttonAdd.setOnClickListener{
            // read the editText value
            val sideString = binding.editTextNumber.text.toString()
            if (sideString.trim().isNotEmpty()) {
                val side = sideString.toInt()
                // if side is not already in an array, then add !!!!
                if (sideArray.contains(side)){
                    Toast.makeText(applicationContext, "${side}-sided die already exists!",Toast.LENGTH_SHORT).show()
                }
                else{
                    sideArray.add(side)
                    sideArray.sort()
                    //Log.i("click", sideArray.toString())
                    adapter.notifyDataSetChanged()
                    Toast.makeText(applicationContext, "${side}-sided die is added",Toast.LENGTH_SHORT).show()
                }
                //editText.setText("0", BufferType.EDITABLE); /*// May be unecessary... */
                binding.editTextNumber.text.clear()
            }
        }

        // 1 roll
        binding.buttonRoll1.setOnClickListener{
            tvResult1.text = ""
            tvResult2.text = die.roll().toString()
            tvResult3.text = ""
            updateTextViewWithValues(tvResult2.text.toString(), "-1", tvHist)
        }
        // 2 rolls
        binding.buttonRoll2.setOnClickListener{
            tvResult1.text = die.roll().toString()
            tvResult2.text = ""
            tvResult3.text = die.roll().toString()
            updateTextViewWithValues(tvResult1.text.toString(), tvResult3.text.toString(), tvHist)
        }

        // clear history
        binding.buttonClear.setOnClickListener{
            allResult = ""
            tvHist.text = allResult
            tvResult1.text = ""
            tvResult2.text = ""
            tvResult3.text = ""
        }
        // remove the added die back to defualt.
        binding.buttonReset.setOnClickListener {
            sideArray = mutableListOf(4, 6, 8, 10, 12, 20)
            adapter =
                ArrayAdapter<Int>(this, android.R.layout.simple_dropdown_item_1line, sideArray)
            adapter.notifyDataSetChanged()
            spinner.adapter = adapter
        }
    }


    // Optional setting/preference layout functions
    override fun onPause() {
        val editor = sharedPref.edit()

        if (sharedPref.getBoolean("pref_save", true)) {
            editor.putInt("spinner", binding.spinner.selectedItemPosition)
            editor.putString("result1", tvResult1.text.toString())
            editor.putString("result2", tvResult2.text.toString())
            editor.putString("result3", tvResult3.text.toString())
            editor.putString("history", allResult)

            // This two lines are from chatgpt
            // Convert the MutableList<Int> to Set<String> of comma-separated values
            val integerSet = sideArray.map { it.toString() }.toSet()
            // Store the Set<String> in SharedPreferences
            editor.putStringSet("integerSet", integerSet)

            editor.apply()
        } else {
            editor.clear()
            editor.putBoolean("pref_save", false)
            editor.apply()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.spinner.getItemAtPosition(sharedPref.getInt("spinner",0) )
        tvResult1.text = sharedPref.getString("result1", "")
        tvResult2.text = sharedPref.getString("result2", "")
        tvResult3.text = sharedPref.getString("result3", "")
        tvHist.text = sharedPref.getString("history", "")
        allResult = tvHist.text.toString()

        // This 4 lines are from chatgpt
        // Retrieve the Set<String> from SharedPreferences
        val integerSet = sharedPref.getStringSet("integerSet", emptySet())

        // Convert the Set<String> to MutableList<Int>
        sideArray  = integerSet?.mapNotNull { it.toIntOrNull() }?.toMutableList() ?: mutableListOf()
        sideArray.sort()

        // Create a new ArrayAdapter with the retrieved integerList
        adapter = ArrayAdapter<Int>(this, android.R.layout.simple_dropdown_item_1line, sideArray )

        // Set the ArrayAdapter to your ListView or other AdapterView
        spinner.adapter = adapter

    }

    private fun updateTextViewWithValues(value1: String, value2: String? = null, textView: TextView) {

        // Add the first value to the string
        allResult += "$value1, "

        // If a second value is provided, add it to the string
        if (value2 != "-1"){
            allResult += "$value2, "
        }

        // Update the text view with the constructed string
        tvHist.text = allResult
    }

    // add menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
            when(item.itemId){
                R.id.setting->{
                    // Activate SettingsActivity layout
                    startActivity(Intent(this, SettingsActivity::class.java))
                    Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show()
                }
            }
            return super.onOptionsItemSelected(item)
    }
}
