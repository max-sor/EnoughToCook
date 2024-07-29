package com.example.enoughtocook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    var productList: MutableList<Product>, // Change to Product
    private val onItemClick: (Product) -> Unit, // Change to Product
    private val updateProductListInViewModel: (MutableList<Product>) -> Unit // Change to Product
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productTextView: TextView = itemView.findViewById(R.id.productTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productTextView.text = "${product.name}, ${product.amount} ${product.type}" // Display product details
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: MutableList<Product>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(productList, newList))
        productList = newList
        diffResult.dispatchUpdatesTo(this)
        updateProductListInViewModel(newList)
    }

    class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].name == newList[newItemPosition].name
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}