package com.example.enoughtocook

import ProductsViewModelFactory
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enoughtocook.databinding.FragmentProductListBinding
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: ProductsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), ProductsViewModelFactory(requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE))).get(ProductsViewModel::class.java)

        adapter = ProductAdapter(mutableListOf(), { product ->
            viewModel.removeProduct(product)
            Toast.makeText(context, "$product was deleted", Toast.LENGTH_LONG).show()
        }, viewModel::updateProductList)

        binding.productRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.productRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productList.collect { list ->
                    adapter.updateList(list.toMutableList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}