package com.example.enoughtocook

import ProductsViewModelFactory
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import androidx.lifecycle.ViewModelProvider


class AddFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ProductsViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val amountData: EditText = view.findViewById(R.id.amountEditText)
        val userData: EditText = view.findViewById(R.id.user_data)
        val typeSpinner: Spinner = view.findViewById(R.id.typeSpinner)
        val button: Button = view.findViewById(R.id.button)


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        viewModel = ViewModelProvider(requireActivity(), ProductsViewModelFactory(sharedPreferences)).get(
            ProductsViewModel::class.java)

        setupSpinner(typeSpinner)

        button.setOnClickListener {
            val name = userData.text.toString().trim().lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val amount = amountData.text.toString().toDoubleOrNull()
            val type = typeSpinner.selectedItem.toString()
            if (name.isNotEmpty() && amount != null) {
                viewModel.addProduct(Product(name, amount, type))
                userData.text.clear()
                amountData.text.clear()
                Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    private fun setupSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.food_types, // Define this array in your strings.xml
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.custom_dropdown_item)
            spinner.adapter = adapter
        }
    }
}