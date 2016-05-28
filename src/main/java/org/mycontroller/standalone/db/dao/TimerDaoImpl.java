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
package org.mycontroller.standalone.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.mycontroller.standalone.AppProperties.RESOURCE_TYPE;
import org.mycontroller.standalone.api.jaxrs.mapper.Query;
import org.mycontroller.standalone.api.jaxrs.mapper.QueryResponse;
import org.mycontroller.standalone.db.tables.AlarmDefinition;
import org.mycontroller.standalone.db.tables.Timer;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.1
 */
public class TimerDaoImpl extends BaseAbstractDaoImpl<Timer, Integer> implements TimerDao {
    public TimerDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Timer.class);
    }

    @Override
    public List<Timer> getAll(RESOURCE_TYPE resourceType, Integer resourceId) {
        QueryBuilder<Timer, Integer> queryBuilder = this.getDao().queryBuilder();
        Where<Timer, Integer> where = queryBuilder.where();
        try {
            where.eq(Timer.KEY_RESOURCE_TYPE, resourceType);
            if (resourceId != null) {
                where.and().eq(Timer.KEY_RESOURCE_ID, resourceId);
            }
            return where.query();
        } catch (SQLException ex) {
            _logger.error("unable to query timers with Resource:[Type:{}, KEY_ID:{}]", resourceType.getText(),
                    resourceId, ex);
            return null;
        }
    }

    @Override
    public List<Timer> getAll(RESOURCE_TYPE resourceType) {
        return this.getAll(resourceType, null);
    }

    @Override
    public void delete(RESOURCE_TYPE resourceType, Integer resourceId) {
        try {
            DeleteBuilder<Timer, Integer> deleteBuilder = this.getDao().deleteBuilder();
            deleteBuilder.where().eq(Timer.KEY_RESOURCE_TYPE, resourceType).and()
                    .eq(Timer.KEY_RESOURCE_ID, resourceId);
            int count = deleteBuilder.delete();
            _logger.debug("deleted timers with Resource:[Type:{}, KEY_ID:{}], Deletion Count:{}",
                    resourceType.getText(),
                    resourceId, count);
        } catch (SQLException ex) {
            _logger.error("unable to delete timers with Resource:[Type:{}, KEY_ID:{}]", resourceType.getText(),
                    resourceId, ex);
        }
    }

    @Override
    public List<Timer> getAllEnabled() {
        try {
            QueryBuilder<Timer, Integer> queryBuilder = this.getDao().queryBuilder();
            queryBuilder.where().eq(Timer.KEY_ENABLED, true);
            List<Timer> timers = this.getDao().query(queryBuilder.prepare());
            return timers;
        } catch (SQLException ex) {
            _logger.error("unable to get all enabled timers", ex);
            return null;
        }
    }

    @Override
    public long countOf(RESOURCE_TYPE resourceType, Integer resourceId) {
        try {
            /*
             GenericRawResults<String[]> rawResults =
                    this.getDao().queryRaw(
                            "SELECT COUNT(*) FROM timer WHERE " + Timer.KEY_RESOURCE_TYPE + " = " + resourceType.ordinal()
                                    + " AND " + Timer.KEY_RESOURCE_ID + " = " + resourceId);
            List<String[]> results = rawResults.getResults();
            return Long.valueOf(results.get(0)[0]);
             */
            QueryBuilder<Timer, Integer> queryBuilder = this.getDao().queryBuilder();
            queryBuilder.where().eq(AlarmDefinition.KEY_RESOURCE_TYPE, resourceType).and()
                    .eq(AlarmDefinition.KEY_RESOURCE_ID, resourceId);
            return queryBuilder.countOf();
        } catch (SQLException ex) {
            _logger.error("unable to get Timers count for resource[Type:{}, Id:{}]", resourceType, resourceId, ex);
        }
        return 0;

    }

    @Override
    public QueryResponse getAll(Query query) {
        try {
            return this.getQueryResponse(query, Timer.KEY_ID);
        } catch (SQLException ex) {
            _logger.error("unable to run query:[{}]", query, ex);
            return null;
        }
    }

    @Override
    public List<Timer> getAll(List<Integer> ids) {
        return getAll(Timer.KEY_ID, ids);
    }

    @Override
    public Timer get(Timer timer) {
        return super.getById(timer.getId());
    }

}
