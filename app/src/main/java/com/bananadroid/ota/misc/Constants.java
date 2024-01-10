/*
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2019 The PixelExperience Project
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
package com.bananadroid.ota.misc;

public final class Constants {
    public static final String AB_PAYLOAD_BIN_PATH = "payload.bin";
    public static final String AB_PAYLOAD_PROPERTIES_PATH = "payload_properties.txt";
    public static final String PREF_METERED_NETWORK_WARNING = "pref_metered_network_warning";
    public static final String UNCRYPT_FILE_EXT = ".uncrypt";
    public static final String PROP_BUILD_TYPE = "ro.banana.edition";
    public static final String PROP_BUILD_DATE = "ro.build.date.utc";
    public static final String PROP_RECOVERY_UPDATE = "persist.sys.recovery_update";
    public static final String PREF_CURRENT_PERSISTENT_STATUS = "current_persistent_status";
    public static final String PREF_INSTALLING_AB_ID = "installing_ab_id";
    public static final String DOWNLOAD_PATH = "/data/banana_updates/";
    static final String PROP_AB_DEVICE = "ro.build.ab_update";
    static final String PROP_DEVICE = "ro.banana.device";
    static final String OTA_URL_VANILLA = "https://raw.githubusercontent.com/bananadroid-devices/official_devices/14/vanilla/%s.json";
    static final String OTA_URL_GAPPS = "https://raw.githubusercontent.com/bananadroid-devices/official_devices/14/gapps/%s.json";
    static final String MAINTAINER_URL = "https://github.com/%s";
    static final String CHANGELOG_URL_VANILLA = "https://raw.githubusercontent.com/bananadroid-devices/official_devices/14/vanilla/%s.md";
    static final String CHANGELOG_URL_GAPPS = "https://raw.githubusercontent.com/bananadroid-devices/official_devices/14/gapps/%s.md";
    static final String EXPORT_PATH = "BananaDroid-Updates/";
}
