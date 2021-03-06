package unidue.rc.workflow;

/*
 * #%L
 * Semesterapparate
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 Universitaet Duisburg Essen
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

import unidue.rc.dao.CommitException;
import unidue.rc.dao.DeleteException;
import unidue.rc.model.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Nils Verheyen
 * @since 05.12.13 09:25
 */
public interface ScannableService {

    /**
     * Creates an {@link Entry} and target {@link Scannable} in backend.
     *
     * @param scannable  scannable to create
     * @param collection collection for the scannable
     * @throws CommitException thrown if the entry and/or scannable could not be saved.
     */
    void create(Scannable scannable, ReserveCollection collection) throws CommitException;

    /**
     * Creates an {@link Entry} and target {@link Scannable} with given {@link Resource} in backend.
     *
     * @param scannable  scannable to create
     * @param collection collection for the scannable
     * @param resource   resource of the scannable
     * @throws CommitException thrown if the entry, scannable and/or resource could not be saved.
     */
    void create(Scannable scannable, ReserveCollection collection, Resource resource) throws CommitException;

    /**
     * Creates a new {@link Scannable}, {@link Entry} and {@link Resource} in backend with given fullTextURL that
     * is bound to target collection.
     *
     * @param scannable   scannable to create
     * @param collection  collection for the scannable
     * @param fullTextURL fulltext for the resource
     * @return the created {@link Resource}
     * @throws CommitException thrown if any object could not be saved in backend
     */
    Resource create(Scannable scannable, ReserveCollection collection, String fullTextURL) throws CommitException;

    /**
     * Creates a new {@link Scannable}, {@link Entry} and {@link Resource} in backend with given input and filename
     * that is bound to target collection.
     *
     * @param scannable  scannable to create
     * @param collection collection for the scannable
     * @param filename   filename for the file
     * @param input      input source
     * @return the created {@link Resource}
     * @throws CommitException thrown if any object could not be saved in backend
     * @throws IOException     thrown during save of the file
     */
    Resource create(Scannable scannable, ReserveCollection collection, String filename, InputStream input) throws CommitException, IOException;

    /**
     * Updates the {@link Resource} of given scannable and sets target full text url. If the scannable contains no
     * resource one is created.
     *
     * @param scannable   scannable to update
     * @param fullTextURL fulltext for the resource
     * @return contains the updated or new created resource
     * @throws CommitException thrown if any object could not be saved in backend
     */
    Resource update(Scannable scannable, String fullTextURL) throws CommitException;

    /**
     * Updates the {@link Resource} of given scannable and sets target input and file name. If the scannable contains no
     * resource one is created.
     *
     * @param scannable scannable to create
     * @param filename  filename for the file
     * @param input     input source
     * @return contains the updated or new created resource
     * @throws CommitException thrown if any object could not be saved in backend
     * @throws IOException     thrown during save of the file
     */
    Resource update(Scannable scannable, String filename, InputStream input) throws CommitException, IOException;

    /**
     * Updates target scannable in backend
     *
     * @param scannable scannable to update
     * @throws CommitException thrown if any object could not be saved in backend
     */
    void update(Scannable scannable) throws CommitException;

    /**
     * Should be called after a {@link ReserveCollection} was updated. Any necessary operations regarding the update
     * of given collection are run via this call.
     *
     * @param collection collection that was update
     * @throws CommitException thrown if any object could not be saved in backend
     */
    void afterCollectionUpdate(ReserveCollection collection) throws CommitException;

    /**
     * Should be called before an {@link Entry} is deleted (not marked as {@link Entry#getDeleted()}).
     *
     * @param entry entry that is going to be deleted
     * @throws DeleteException thrown if any object could not be deleted in backend.
     */
    void beforeEntryDelete(Entry entry) throws DeleteException;

    /**
     * Should be called after an {@link Entry} was deleted (not marked as {@link Entry#getDeleted()}).
     *
     * @param entry entry that was deleted
     */
    void afterEntryDelete(Entry entry);

    /**
     * Should be called after an {@link Entry} was updated in backend.
     *
     * @param entry entry that was updated
     * @throws CommitException thrown if any object could not be saved in backend
     */
    void afterEntryUpdate(Entry entry) throws CommitException;

    /**
     * Creates a clone of target {@link Scannable} with all copied meta data. References to other objects are not
     * cloned. Cloning is currently only allowed inside the same {@link ReserveCollection}.
     *
     * @param scannable  scannable to duplicate
     * @param collection target collection
     * @param <T>        scannable to duplicate
     * @return the cloned scannable
     * @throws UnsupportedOperationException if the collection of given scannable and given collection do not match.
     */
    <T extends Scannable> T duplicate(T scannable, ReserveCollection collection);

    /**
     * Deletes the {@link java.io.File} that may be referenced through the {@link Resource} of target {@link Scannable}.
     * Additional workflow operations are called cause of deletion of the file.
     *
     * @param scannable scannable to use
     * @throws CommitException thrown if any object could not be saved in backend
     */
    void deleteFile(Scannable scannable) throws CommitException;
}
