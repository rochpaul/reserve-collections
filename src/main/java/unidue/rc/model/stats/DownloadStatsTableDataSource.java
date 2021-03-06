package unidue.rc.model.stats;

import java.util.List;
import java.util.Set;

/*
 * #%L
 * Semesterapparate
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Universitaet Duisburg Essen
 * %%
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
 * #L%
 */

/**
 * Created by marcus.koesters on 08.09.15. interface for using datasources / wrapper classes with
 * DownloadsStatsTable-Component
 */
public interface DownloadStatsTableDataSource {



     void addFile(Integer key, StatisticFile file);

     Set<Integer> getResourceIds();

     StatisticFile getFile(Integer key);

     Integer getTotalHitCount();

     public List<DownloadDate> getDownloadDates();

}
