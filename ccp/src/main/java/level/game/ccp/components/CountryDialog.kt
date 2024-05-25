package level.game.ccp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.collections.immutable.persistentListOf
import level.game.ccp.R
import level.game.ccp.data.CountryData
import level.game.ccp.data.utils.countryNames
import level.game.ccp.data.utils.emojiFlag
import level.game.ccp.data.utils.searchCountry

internal val DEFAULT_ROUNDING = 10.dp
private val DEFAULT_ROW_PADDING = 16.dp
private const val ROW_PADDING_VERTICAL_SCALING = 1.1f
private val SEARCH_ICON_PADDING = 5.dp
private const val HEADER_TEXT_SIZE_MULTIPLE = 1.5
private val MIN_TAP_DIMENSION = 48.dp
private const val DIVIDER_ALPHA = 0.12f

/**
 * @param onDismissRequest Executes when the user tries to dismiss the dialog.
 * @param onSelect Executes when the user selects a country from the list.
 * @param textStyle A [TextStyle] for customizing text style of search input field and country rows.
 * @param modifier The modifier to be applied to the dialog surface.
 * @param countryList The list of countries to display in the dialog.
 * @param rowPadding The padding to be applied to each row.
 * @param backgroundColor The [Color] of the dialog background.
 * @param dividerColor The [Color] of the country row dividers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDialog(
    onDismissRequest: () -> Unit,
    onSelect: (item: CountryData) -> Unit,
    textStyle: androidx.compose.material3.Typography,
    modifier: Modifier = Modifier,
    countryList: List<CountryData> = persistentListOf(),
    rowPadding: Dp = DEFAULT_ROW_PADDING,
    backgroundColor: Color = Color((0XFF282234)),
    dividerColor: Color ,
    textColor: Color
) {
    val context = LocalContext.current
    var searchValue by rememberSaveable { mutableStateOf("") }
    val filteredCountries by remember(context, searchValue) {
        derivedStateOf {
            if (searchValue.isEmpty()) {
                countryList
            } else {
                countryList.searchCountry(
                    searchValue,
                    context,
                )
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = backgroundColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderRow(textStyle, onDismissRequest, textColor =textColor )
            SearchTextField(
                value = searchValue,
                onValueChange = { searchValue = it },
                textStyle = textStyle,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search),
                        tint = textColor,
                        modifier = Modifier.padding(horizontal = SEARCH_ICON_PADDING),
                    )
                },
                textColor = textColor
            )
            Spacer(modifier = Modifier.height(DEFAULT_ROW_PADDING))
            LazyColumn {
                items(filteredCountries, key = { it.countryIso }) { countryItem ->
                    HorizontalDivider(color = dividerColor)
                    CountryRowItem(
                        rowPadding = rowPadding,
                        onSelect = { onSelect(countryItem) },
                        countryItem = countryItem,
                        textStyle = textStyle,
                        textColor
                    )
                }
            }
        }

    }

//    Dialog(
//        onDismissRequest = onDismissRequest,
//        content = {
//            @Suppress("ReusedModifierInstance")
//            Surface(
//                color = backgroundColor,
//                modifier = modifier,
//            ) {
//            }
//        },
//    )
}

@Composable
private fun HeaderRow(
    textStyle: Typography,
    onDismissRequest: () -> Unit,
    textColor : Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(DEFAULT_ROW_PADDING))
        Text(
            text = stringResource(id = R.string.select_country),
            color = textColor
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onDismissRequest() },
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Close",
                tint = textColor
            )
        }
    }
}

@Composable
private fun CountryRowItem(
    rowPadding: Dp,
    onSelect: () -> Unit,
    countryItem: CountryData,
    textStyle: androidx.compose.material3.Typography,
    color : Color
) {
    Row(
        Modifier
            .clickable(onClick = { onSelect() })
            .padding(
                horizontal = rowPadding,
                vertical = rowPadding * ROW_PADDING_VERTICAL_SCALING,
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = countryItem.emojiFlag + "  " +
                    stringResource(
                        id = countryNames.getOrDefault(
                            countryItem.countryIso,
                            R.string.unknown,
                        ),
                    ),
            style = textStyle.labelMedium,
            overflow = TextOverflow.Ellipsis,
            color = color
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.material3.Typography,
    leadingIcon: (@Composable () -> Unit)? = null,
    hint: String = stringResource(id = R.string.search),
    textColor: Color
) {
    val requester = remember { FocusRequester() }


    BasicTextField(
         modifier = Modifier
            .padding(horizontal = DEFAULT_ROW_PADDING)
            .height(MIN_TAP_DIMENSION)
            .fillMaxWidth()
            .focusRequester(requester),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(textColor),
        textStyle = textStyle.labelMedium.copy(
            color = textColor
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                Box(
                    modifier = Modifier
                        .padding(start = DEFAULT_ROUNDING)
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            maxLines = 1,
                            style = textStyle.labelMedium
                        )
                    }
                innerTextField()
                }
            }
        },
    )
}
