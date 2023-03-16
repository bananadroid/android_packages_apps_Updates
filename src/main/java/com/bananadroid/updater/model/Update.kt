/*
 * Copyright (C) 2023 BananaDroid
 * Copyright (C) 2023 Andrea Canale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bananadroid.updater.Model

data class Update(
    val download_url: String,
    val phone: String,
    val changelog_source: String,
    val changelog_device: String,
    val version: String,
    val photo: String
)