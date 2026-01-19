package com.pepsigo.admin.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ProfileDetailsContent(
    profile: UserProfileUiModel,
    onEditEmailClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading below topbar (acts as card heading)
//        Spacer(modifier = Modifier.height(8.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
//            modifier = modifier.fillMaxWidth()
        ){
            Text(
                text = "Manage your account details",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
//            Spacer(modifier = Modifier.padding(8.dp))
            IconButton(onClick = onEditProfileClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Email")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Card with profile details
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileItem(
                    icon = Icons.Default.Business,
                    label = "Business Name",
                    value = profile.businessName
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItem(
                    icon = Icons.Default.Person,
                    label = "Name",
                    value = profile.name
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Mobile",
                    value = profile.mobile
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email with edit icon
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = profile.email,
                    onEditClick = onEditEmailClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Address1",
                    value = profile.address1
                )

                Spacer(modifier = Modifier.height(16.dp))
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Address2",
                    value = profile.address2
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "State",
                    value = profile.state
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Pincode",
                    value = profile.pincode
                )
                Spacer(modifier = Modifier.height(16.dp))

//                ProfileItem(
//                    icon = Icons.Default.Map,
//                    label = "Latitude",
//                    value = profile.latitude
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//
//                ProfileItem(
//                    icon = Icons.Default.Map,
//                    label = "Longitude",
//                    value = profile.longitude
//                )
//                Spacer(modifier = Modifier.height(20.dp))

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Change Password button with lock icon
        Button(
            onClick = onChangePasswordClick,
            modifier = Modifier.size(225.dp, 45.dp)
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock")
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = "Change Password")
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Show edit button if edit action provided
        if (onEditClick != null) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit $label"
                )
            }
        }
    }
}