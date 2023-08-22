/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zetasql.toolkit.antipattern.cmd;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.zetasql.toolkit.antipattern.util.BigQueryHelper;
import java.util.Iterator;
import java.util.Optional;

public class InformationSchemaQueryIterable implements Iterator<InputQuery> {

  Iterator<FieldValueList> fieldValueListIterator;
  String IS_TABLE_DEFAULT = "`region-us`.INFORMATION_SCHEMA.JOBS";
  String DAYS_BACK_DEFAULT = "30";

  public InformationSchemaQueryIterable(String projectId, Long timeoutInSecs) throws InterruptedException {
    TableResult tableResult =
        BigQueryHelper.getQueriesFromIS(projectId, timeoutInSecs, DAYS_BACK_DEFAULT, IS_TABLE_DEFAULT);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  public InformationSchemaQueryIterable(String projectId, Long timeoutInSecs, String daysBack)
      throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueriesFromIS(projectId, timeoutInSecs, daysBack, IS_TABLE_DEFAULT);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  public InformationSchemaQueryIterable(String projectId, Long timeoutInSecs, String daysBack, String ISTable)
      throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueriesFromIS(projectId, timeoutInSecs, daysBack, ISTable);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  public InformationSchemaQueryIterable(String projectId, Long timeoutInSecs, String startTime, String endTime, String ISTable)
      throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueriesFromIS(projectId, timeoutInSecs, startTime, endTime,
            Optional.ofNullable(ISTable).orElse(IS_TABLE_DEFAULT));
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  @Override
  public boolean hasNext() {
    return fieldValueListIterator.hasNext();
  }

  @Override
  public InputQuery next() {
    FieldValueList row = fieldValueListIterator.next();
    String job_id = row.get("job_id").getStringValue();
    String query = row.get("query").getStringValue();
    String projectId = row.get("project_id").getStringValue();
    String slot_hours = row.get("slot_hours").getStringValue();
    return new InputQuery(query, job_id, projectId, Float.parseFloat(slot_hours));
  }
}
