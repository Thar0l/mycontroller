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
package org.mycontroller.standalone.alarm;

import java.util.Date;
import java.util.List;

import org.mycontroller.standalone.MycUtils;
import org.mycontroller.standalone.db.DaoUtils;
import org.mycontroller.standalone.db.ResourcesLogsUtils;
import org.mycontroller.standalone.db.ResourcesLogsUtils.LOG_LEVEL;
import org.mycontroller.standalone.db.tables.AlarmDefinition;
import org.mycontroller.standalone.db.tables.Gateway;
import org.mycontroller.standalone.db.tables.Node;
import org.mycontroller.standalone.db.tables.Notification;
import org.mycontroller.standalone.db.tables.ResourcesGroup;
import org.mycontroller.standalone.db.tables.SensorVariable;
import org.mycontroller.standalone.model.ResourceModel;
import org.mycontroller.standalone.notification.INotificationEngine;
import org.mycontroller.standalone.notification.NotificationEmail;
import org.mycontroller.standalone.notification.NotificationPushbulletNote;
import org.mycontroller.standalone.notification.NotificationSMS;
import org.mycontroller.standalone.notification.NotificationSendPayLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.1
 */
@Builder
public class AlarmEngine implements Runnable {
    private static final Logger _logger = LoggerFactory.getLogger(AlarmEngine.class);

    private List<AlarmDefinition> alarmDefinitions;
    private Object resourceObject;

    public AlarmEngine(List<AlarmDefinition> alarmDefinitions, Object resourceObject) {
        this.alarmDefinitions = alarmDefinitions;
        this.resourceObject = resourceObject;
    }

    public AlarmEngine(List<AlarmDefinition> alarmDefinitions) {
        this(alarmDefinitions, null);
    }

    public void runAlarm(AlarmDefinition alarmDefinition) throws Exception {
        boolean triggerAlarm = false;
        String thresholdValue = null;
        Object actualValueObject = this.resourceObject;
        String actualValue = null;
        if (actualValueObject == null) {
            actualValueObject = new ResourceModel(alarmDefinition.getResourceType(),
                    alarmDefinition.getResourceId()).getResource();
        }
        switch (alarmDefinition.getResourceType()) {
            case SENSOR_VARIABLE:
                actualValue = ((SensorVariable) actualValueObject).getValue();
                break;
            /* NOTE: not using other than sensor variable, below code may not hit when we configure GUI */
            case NODE:
                actualValue = ((Node) actualValueObject).getState().getText();
                break;
            case GATEWAY:
                actualValue = ((Gateway) actualValueObject).getState().getText();
                break;
            case RESOURCES_GROUP:
                actualValue = ((ResourcesGroup) actualValueObject).getState().getText();
                break;
            default:
                break;
        }
        switch (alarmDefinition.getThresholdType()) {
            case VALUE:
                thresholdValue = alarmDefinition.getThresholdValue();
                break;
            case SENSOR_VARIABLE:
                SensorVariable thresholdSensorValue = DaoUtils.getSensorVariableDao().get(
                        MycUtils.getInteger(alarmDefinition.getThresholdValue()));
                if (thresholdSensorValue != null) {
                    thresholdValue = thresholdSensorValue.getValue();
                }
                break;
            /* NOTE: not using other than sensor variable, below code may not hit when we configure GUI */
            case GATEWAY_STATE:
                Gateway gateway = DaoUtils.getGatewayDao().getById(
                        MycUtils.getInteger(alarmDefinition.getThresholdValue()));
                if (gateway != null) {
                    thresholdValue = gateway.getState().getText();
                }
                break;
            case NODE_STATE:
                Node node = DaoUtils.getNodeDao()
                        .getById(MycUtils.getInteger(alarmDefinition.getThresholdValue()));
                if (node != null) {
                    thresholdValue = node.getState().getText();
                }
                break;
            default:
                break;

        }
        if (thresholdValue == null) {
            _logger.warn("Could not execute this item, it does not have threshold value! AlarmDefinition:[{}]",
                    alarmDefinition);
            return;
        }
        switch (alarmDefinition.getTriggerType()) {
            case EQUAL:
                if (actualValue.equals(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            case GREATER_THAN:
                if (Double.parseDouble(actualValue) > Double.parseDouble(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            case GREATER_THAN_EQUAL:
                if (Double.parseDouble(actualValue) >= Double.parseDouble(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            case LESSER_THAN:
                if (Double.parseDouble(actualValue) < Double.parseDouble(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            case LESSER_THAN_EQUAL:
                if (Double.parseDouble(actualValue) <= Double.parseDouble(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            case NOT_EQUAL:
                if (!actualValue.equals(thresholdValue)) {
                    triggerAlarm = true;
                }
                break;
            default:
                break;
        }
        //Dampening Mode
        switch (alarmDefinition.getDampeningType()) {
            case NONE:
                alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                break;
            case CONSECUTIVE:
                DampeningConsecutive dampeningConsecutive = new DampeningConsecutive(alarmDefinition);
                if (triggerAlarm) {
                    dampeningConsecutive.incrementConsecutiveCount();
                } else {
                    dampeningConsecutive.setConsecutiveCount(0);
                    alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                }
                if (dampeningConsecutive.evaluate()) {
                    dampeningConsecutive.reset();
                    alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                }
                break;
            case LAST_N_EVALUATIONS:
                DampeningLastNEvaluations lastNEvaluations = new DampeningLastNEvaluations(alarmDefinition);
                lastNEvaluations.incrementEvaluationsCount();
                if (triggerAlarm) {
                    lastNEvaluations.incrementOccurrencesCount();
                }
                if (lastNEvaluations.isExecutable()) {
                    if (lastNEvaluations.evaluate()) {
                        alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                    } else {
                        alarmTriggerCall(alarmDefinition, actualValue, false);
                    }
                    lastNEvaluations.reset();
                }
                break;
            case ACTIVE_TIME:
                DampeningActiveTime dampeningActiveTime = new DampeningActiveTime(alarmDefinition);
                if (triggerAlarm) {
                    if (dampeningActiveTime.getActiveFrom() == DampeningActiveTime.ACTIVE_FROM_RESET_VALUE) {//Set active as current time
                        dampeningActiveTime.updateActiveFrom(System.currentTimeMillis());
                    } else if (dampeningActiveTime.evaluate()) {
                        alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                    }
                } else {
                    dampeningActiveTime.reset();
                    alarmTriggerCall(alarmDefinition, actualValue, triggerAlarm);
                }
                break;
            default:
                break;
        }
        DaoUtils.getAlarmDefinitionDao().update(alarmDefinition);

    }

    private void alarmTriggerCall(AlarmDefinition alarmDefinition, String actualValue, boolean triggerAlarm)
            throws Exception {
        if (triggerAlarm) {
            if (!alarmDefinition.getTriggered() || !alarmDefinition.getIgnoreDuplicate()) {
                List<Notification> notifications = DaoUtils.getNotificationDao().getByAlarmDefinitionIdEnabled(
                        alarmDefinition.getId());

                INotificationEngine notificationEngine = null;
                for (Notification notification : notifications) {
                    switch (notification.getType()) {
                        case SEND_PAYLOAD:
                            notificationEngine = NotificationSendPayLoad.builder().alarmDefinition(alarmDefinition)
                                    .notification(notification).actualValue(actualValue).build().update();
                            break;
                        case SEND_EMAIL:
                            notificationEngine = NotificationEmail.builder().alarmDefinition(alarmDefinition)
                                    .notification(notification).actualValue(actualValue).build().update();
                            break;
                        case SEND_SMS:
                            notificationEngine = NotificationSMS.builder().alarmDefinition(alarmDefinition)
                                    .notification(notification).actualValue(actualValue).build().update();
                            break;
                        case PUSHBULLET_NOTE:
                            notificationEngine = NotificationPushbulletNote.builder().alarmDefinition(alarmDefinition)
                                    .notification(notification).actualValue(actualValue).build().update();
                            break;
                        default:
                            _logger.info("Not implemented this type:{}", notification.getType()
                                    .getText());
                            break;
                    }
                    if (notificationEngine != null) {
                        notificationEngine.execute();
                    } else {
                        _logger.warn("Notification did not executed for AlarmDefination:[{}]", alarmDefinition);
                    }
                }
                alarmDefinition.setTriggered(true);
                if (alarmDefinition.getDisableWhenTrigger()) {
                    alarmDefinition.setEnabled(false);
                }
                //AlarmDefinition Triggered Message, ResourcesLogs message data
                ResourcesLogsUtils.setAlarmLog(LOG_LEVEL.INFO, alarmDefinition, triggerAlarm, null);
                //Update Last Trigger Time
                alarmDefinition.setLastTrigger(new Date().getTime());
                DaoUtils.getAlarmDefinitionDao().update(alarmDefinition);
            }
        } else {
            if (alarmDefinition.getTriggered()) {
                alarmDefinition.setTriggered(false);
                DaoUtils.getAlarmDefinitionDao().update(alarmDefinition);
            }
        }
    }

    @Override
    public void run() {
        if (alarmDefinitions == null) {
            _logger.debug("Nothing to do with alarm definitions with null referance");
            return;
        }
        for (AlarmDefinition alarmDefinition : alarmDefinitions) {
            try {
                if (alarmDefinition.getEnabled()) {
                    runAlarm(alarmDefinition);
                } else {
                    _logger.debug("AlarmDefinition[{}] disabled, no action needed.");
                }
            } catch (Exception ex) {
                _logger.error("failed to execute alarm:[{}],", alarmDefinition, ex);
                ResourcesLogsUtils.setAlarmLog(LOG_LEVEL.ERROR, alarmDefinition, null, ex.getMessage());
            }
        }
    }

}
