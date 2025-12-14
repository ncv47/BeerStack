package com.example.beerstack.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.beerstack.model.Currency

//The sort functionality in a dropdown menu
@Composable
fun SortDropdown(
    selectedSort: SortOptions,
    onSortChange: (SortOptions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }  // track dropdown open/closed

    Box {
        // Small pill-shaped button showing current sort
        FilledTonalButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.height(40.dp) // close to searchbar height but smaller
        ) {
            Text("Sort: ${selectedSort.label}") // show current selection
        }

        DropdownMenu(
            expanded = expanded,                    // open state
            onDismissRequest = { expanded = false } // close when clicked outside
        ) {
            SortOptions.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) }, // label for each option
                    onClick = {
                        onSortChange(option)    // update selection
                        expanded = false        // close menu
                    }
                )
            }
        }
    }
}


//Searchbar functionality
@Composable
fun SearchBar(
    value: String,                      // current search text
    onValueChange: (String) -> Unit,    // called when text changes
    onSearch: () -> Unit,               // called when search action is triggered
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange, // update state on typing
        placeholder = { Text("Search beers...") },
        modifier = modifier
            .height(56.dp)
            .padding(end = 0.dp),
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),   // shows "Search" button on keyboard for the user
        keyboardActions = KeyboardActions(onSearch = { onSearch() })                    // trigger on search
    )
}

//For the DOLLAR to EURO conversion with API
@Composable
fun CurrencyToggle(
    currency: Currency,             // current currency
    onToggleAndRefresh: () -> Unit  // called when toggled
) {
    FilledTonalButton(
        onClick = onToggleAndRefresh,
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier
            .height(40.dp)
            .width(28.dp)
    ) {
        Text(if (currency == Currency.USD) "$" else "€")
    }
}

//Options for the sort function
enum class SortOptions(val label: String) {
    NAME("A-Z"),
    NAME_REVERSE("Z-A"),
    PRICE("Price ↑"),
    PRICE_REVERSE("Price ↓"),
    RATING("Rating ↑"),
    RATING_REVERSE("Rating ↓")
}