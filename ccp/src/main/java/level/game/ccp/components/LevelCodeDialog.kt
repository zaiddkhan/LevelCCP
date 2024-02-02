package level.game.ccp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableSet
import level.game.ccp.data.CountryData
import level.game.ccp.data.utils.emojiFlag
import level.game.ccp.data.utils.sortedByLocalizedName


internal val DEFAULT_PADDING = 10.dp

@Composable
fun LevelCodeDialog(
    selectedCountry: CountryData,
    includeOnly: ImmutableSet<String>?,
    onCountryChange: (CountryData) -> Unit,
    showCountryCode: Boolean,
    showFlag: Boolean,
    textStyle: Typography,
    backgroundColor: Color,
    iconColor : Color,
    textColor : Color,
    dividerColor : Color
) {
    val context = LocalContext.current

    var country by remember { mutableStateOf(selectedCountry) }
    var isOpenDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val countryList by remember(context, includeOnly) {
        derivedStateOf {
            val allCountries = CountryData.entries.sortedByLocalizedName(context)
            includeOnly?.run {
                val includeUppercase = map { it.uppercase() }
                allCountries.filter { it.countryIso in includeUppercase }
            } ?: allCountries
        }
    }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                isOpenDialog = true
            },
    ) {
        CountryRow(
            modifier = Modifier.padding(DEFAULT_PADDING),
            showCountryCode = showCountryCode,
            showFlag = showFlag,
            country = country,
            textStyle = textStyle
        )

        if (isOpenDialog) {
            CountryDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                onDismissRequest = { isOpenDialog = false },
                onSelect = { countryItem ->
                    onCountryChange(countryItem)
                    country = countryItem
                    isOpenDialog = false
                },
                countryList = countryList,
                textStyle = textStyle,
                backgroundColor = backgroundColor,
                textColor =textColor ,
                dividerColor = dividerColor
            )
        }
    }
}

@Composable
private fun CountryRow(
    showCountryCode: Boolean,
    showFlag: Boolean,
    country: CountryData,
    textStyle: Typography,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = emojiCodeText(
            showFlag = showFlag,
            isPickCountry = country,
            showCountryCode = showCountryCode,
        ),
        modifier = Modifier.padding(start = DEFAULT_PADDING),
        style = textStyle.labelMedium,
    )
}

@Composable
private fun emojiCodeText(
    showFlag: Boolean,
    isPickCountry: CountryData,
    showCountryCode: Boolean,
) = (if (showFlag) isPickCountry.emojiFlag else "") +
        (if (showCountryCode && showFlag) "  " else "") +
        (if (showCountryCode) isPickCountry.countryPhoneCode else "")


