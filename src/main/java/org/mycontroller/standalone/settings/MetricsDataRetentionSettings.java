/**
 * Copyright (C) 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.settings;

import org.mycontroller.standalone.MycUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.3
 */
@Builder
@ToString(includeFieldNames = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsDataRetentionSettings {
    public static final String KEY_METRICS_DATA_RETENTION = "metricsDataRetention";

    public static final String SKEY_RETENTION_RAW_DATA = "aggregationRawData";
    public static final String SKEY_RETENTION_ONE_MINUTE = "aggregationOneMinute";
    public static final String SKEY_RETENTION_FIVE_MINUTES = "aggregationFiveMinutes";
    public static final String SKEY_RETENTION_ONE_HOUR = "aggregationOneHour";
    public static final String SKEY_RETENTION_SIX_HOURS = "aggregationSixHours";
    public static final String SKEY_RETENTION_TWELVE_HOURS = "aggregationTwelveHours";
    public static final String SKEY_RETENTION_ONE_DAY = "aggregationOneDay";

    public static final String SKEY_LAST_AGGREGATION_RAW_DATA = "lastAggregationRawData";
    public static final String SKEY_LAST_AGGREGATION_ONE_MINUTE = "lastAggregationOneMinute";
    public static final String SKEY_LAST_AGGREGATION_FIVE_MINUTES = "lastAggregationFiveMinutes";
    public static final String SKEY_LAST_AGGREGATION_ONE_HOUR = "lastAggregationOneHour";
    public static final String SKEY_LAST_AGGREGATION_SIX_HOURS = "lastAggregationSixHours";
    public static final String SKEY_LAST_AGGREGATION_TWELVE_HOURS = "lastAggregationTwelveHours";
    public static final String SKEY_LAST_AGGREGATION_ONE_DAY = "lastAggregationOneDay";

    private Long lastAggregationRawData;
    private Long lastAggregationOneMinute;
    private Long lastAggregationFiveMinutes;
    private Long lastAggregationOneHour;
    private Long lastAggregationSixHours;
    private Long lastAggregationTwelveHours;
    private Long lastAggregationOneDay;

    private Long retentionRawData;
    private Long retentionOneMinute;
    private Long retentionFiveMinutes;
    private Long retentionOneHour;
    private Long retentionSixHours;
    private Long retentionTwelveHours;
    private Long retentionOneDay;

    public static MetricsDataRetentionSettings get() {
        return MetricsDataRetentionSettings.builder()
                .retentionRawData(MycUtils.getLong(getValue(SKEY_RETENTION_RAW_DATA)))
                .retentionOneMinute(MycUtils.getLong(getValue(SKEY_RETENTION_ONE_MINUTE)))
                .retentionFiveMinutes(MycUtils.getLong(getValue(SKEY_RETENTION_FIVE_MINUTES)))
                .retentionOneHour(MycUtils.getLong(getValue(SKEY_RETENTION_ONE_HOUR)))
                .retentionSixHours(MycUtils.getLong(getValue(SKEY_RETENTION_SIX_HOURS)))
                .retentionTwelveHours(MycUtils.getLong(getValue(SKEY_RETENTION_TWELVE_HOURS)))
                .retentionOneDay(MycUtils.getLong(getValue(SKEY_RETENTION_ONE_DAY)))
                .lastAggregationRawData(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_RAW_DATA)))
                .lastAggregationOneMinute(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_ONE_MINUTE)))
                .lastAggregationFiveMinutes(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_FIVE_MINUTES)))
                .lastAggregationOneHour(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_ONE_HOUR)))
                .lastAggregationSixHours(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_SIX_HOURS)))
                .lastAggregationTwelveHours(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_TWELVE_HOURS)))
                .lastAggregationOneDay(MycUtils.getLong(getValue(SKEY_LAST_AGGREGATION_ONE_DAY)))
                .build();
    }

    public void save() {
        if (retentionRawData != null) {
            updateValue(SKEY_RETENTION_RAW_DATA, retentionRawData);
        }
        if (retentionOneMinute != null) {
            updateValue(SKEY_RETENTION_ONE_MINUTE, retentionOneMinute);
        }
        if (retentionFiveMinutes != null) {
            updateValue(SKEY_RETENTION_FIVE_MINUTES, retentionFiveMinutes);
        }
        if (retentionOneHour != null) {
            updateValue(SKEY_RETENTION_ONE_HOUR, retentionOneHour);
        }
        if (retentionSixHours != null) {
            updateValue(SKEY_RETENTION_SIX_HOURS, retentionSixHours);
        }
        if (retentionTwelveHours != null) {
            updateValue(SKEY_RETENTION_TWELVE_HOURS, retentionTwelveHours);
        }
        if (retentionOneDay != null) {
            updateValue(SKEY_RETENTION_ONE_DAY, retentionOneDay);
        }
    }

    @JsonIgnore
    public void updateInternal() {
        if (lastAggregationRawData != null) {
            updateValue(SKEY_LAST_AGGREGATION_RAW_DATA, lastAggregationRawData);
        }
        if (lastAggregationOneMinute != null) {
            updateValue(SKEY_LAST_AGGREGATION_ONE_MINUTE, lastAggregationOneMinute);
        }
        if (lastAggregationFiveMinutes != null) {
            updateValue(SKEY_LAST_AGGREGATION_FIVE_MINUTES, lastAggregationFiveMinutes);
        }
        if (lastAggregationOneHour != null) {
            updateValue(SKEY_LAST_AGGREGATION_ONE_HOUR, lastAggregationOneHour);
        }
        if (lastAggregationSixHours != null) {
            updateValue(SKEY_LAST_AGGREGATION_SIX_HOURS, lastAggregationSixHours);
        }
        if (lastAggregationTwelveHours != null) {
            updateValue(SKEY_LAST_AGGREGATION_TWELVE_HOURS, lastAggregationTwelveHours);
        }
        if (lastAggregationOneDay != null) {
            updateValue(SKEY_LAST_AGGREGATION_ONE_DAY, lastAggregationOneDay);
        }
    }

    private static String getValue(String subKey) {
        return SettingsUtils.getValue(KEY_METRICS_DATA_RETENTION, subKey);
    }

    private static void updateValue(String subKey, Object value) {
        SettingsUtils.updateValue(KEY_METRICS_DATA_RETENTION, subKey, value);
    }

}
