package studio.evolune.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shared page card; not a focusable fake interaction. */
@Composable
internal fun EvolunePanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color=EvoluneTokens.Paper,shape=RoundedCornerShape(20.dp),
        border=BorderStroke(1.dp,EvoluneTokens.Line),modifier=Modifier.fillMaxWidth()
    ) { Column(Modifier.padding(18.dp),content=content) }
}

/** Native Material field with uniform errors, disabled state and visible focus. */
@Composable
internal fun EvoluneInput(
    value: String, onValueChange: (String)->Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if(singleLine) 1 else Int.MAX_VALUE,
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value=value,onValueChange=onValueChange,
        label={Text(label,fontSize=14.sp)},
        placeholder=if(placeholder==null) null else {
            { Text(placeholder,color=EvoluneTokens.Muted,fontSize=14.sp) }
        },
        modifier=modifier.fillMaxWidth(),
        enabled=enabled,isError=isError,
        singleLine=singleLine,minLines=minLines,maxLines=maxLines,
        keyboardOptions=keyboardOptions,
        shape=RoundedCornerShape(13.dp),
        colors=OutlinedTextFieldDefaults.colors(
            focusedBorderColor=EvoluneTokens.Forest,unfocusedBorderColor=Color(0xFF9EAFA1),
            errorBorderColor=Color(0xFF905C3E),
            errorLabelColor=Color(0xFF905C3E),
            focusedContainerColor=EvoluneTokens.Paper,unfocusedContainerColor=EvoluneTokens.Paper,
            disabledContainerColor=EvoluneTokens.Pale
        )
    )
}

/** Shared icon-free chip: proper semantic selected state and a 48dp target. */
@Composable
internal fun EvoluneChoice(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true
) {
    FilterChip(
        selected=selected,onClick=onClick,enabled=enabled,
        label={Text(label,fontSize=14.sp,lineHeight=20.sp)},
        modifier=Modifier.heightIn(min=48.dp),
        shape=RoundedCornerShape(12.dp),
        colors=FilterChipDefaults.filterChipColors(
            containerColor=EvoluneTokens.Paper,labelColor=EvoluneTokens.Forest,
            selectedContainerColor=EvoluneTokens.Pale,selectedLabelColor=EvoluneTokens.Forest,
            disabledContainerColor=EvoluneTokens.Pale,disabledLabelColor=EvoluneTokens.Muted
        ),
        border=FilterChipDefaults.filterChipBorder(
            enabled=enabled,selected=selected,
            borderColor=Color(0xFFC8D5C9),selectedBorderColor=EvoluneTokens.Forest
        )
    )
}

/** A destructive action is never performed until the person confirms it. */
@Composable
internal fun EvoluneDestructiveDialog(
    title: String, message: String, actionLabel: String,
    onConfirm: ()->Unit, onCancel: ()->Unit
) {
    AlertDialog(
        onDismissRequest=onCancel,
        title={Text(title,color=EvoluneTokens.Ink,fontSize=21.sp)},
        text={Text(message,color=EvoluneTokens.Muted,fontSize=14.sp,lineHeight=21.sp)},
        confirmButton={
            TextButton(onClick=onConfirm,modifier=Modifier.heightIn(min=48.dp)) {
                Text(actionLabel,color=Color(0xFF905C3E),
                    fontSize=14.sp,fontWeight=FontWeight.SemiBold)
            }
        },
        dismissButton={
            TextButton(onClick=onCancel,modifier=Modifier.heightIn(min=48.dp)) {
                Text("Keep reflection",color=EvoluneTokens.Forest,fontSize=14.sp)
            }
        },
        containerColor=EvoluneTokens.Paper,
        shape=RoundedCornerShape(20.dp)
    )
}
