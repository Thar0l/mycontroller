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
package org.mycontroller.standalone;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.1
 */
public class TIME_REF {
    public static final long TEN_MILLISECONDS = 10;
    public static final long ONE_SECOND = TEN_MILLISECONDS * 100;
    public static final long ONE_MINUTE = ONE_SECOND * 60;   // 1 minute
    public static final long FIVE_MINUTES = ONE_MINUTE * 5;  // 5 minute
    public static final long THREE_MINUTES = ONE_MINUTE * 3; // 5 minute    
    public static final long ONE_HOUR = ONE_MINUTE * 60;     // 1 hour
    public static final long ONE_DAY = ONE_HOUR * 24;        // 1 day
    public static final long ONE_YEAR = ONE_DAY * 365;       // 1 year
}
