import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.example.enoughtocook.ProductsViewModel

class ProductsViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}