package unidue.rc.ui.pages.jobs;

/**
 * Copyright (C) 2014 - 2016 Universitaet Duisburg-Essen (semapp|uni-due.de)
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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;

import unidue.rc.dao.LibraryLocationDAO;
import unidue.rc.model.CopyrightReviewStatus;
import unidue.rc.model.LibraryLocation;
import unidue.rc.model.solr.SolrCopyrightView;
import unidue.rc.model.solr.SolrScanJobView;
import unidue.rc.search.SolrNumberQueryField;
import unidue.rc.search.SolrQueryBuilder;
import unidue.rc.search.SolrQueryField;
import unidue.rc.search.SolrResponse;
import unidue.rc.search.SolrService;
import unidue.rc.search.SolrSortField;
import unidue.rc.ui.ProtectedPage;
import unidue.rc.ui.components.Pagination;
import unidue.rc.ui.selectmodel.LibraryLocationSelectModel;
import unidue.rc.ui.valueencoder.LibraryLocationValueEncoder;

@ProtectedPage
public class CopyrightReviewJobs {

	// private enum BlockDefinition {
	// Batch, EditJob
	// }

	@Inject
	private Logger log;

	// solr
	@Inject
	private SolrService solrService;

	@Property
	@Persist
	private String query;

	@Property
	private SolrCopyrightView review;

	// other
	@Inject
	private LibraryLocationDAO libraryLocationDAO;

	@Inject
	private Request request;

	@Inject
	private AjaxResponseRenderer ajaxRenderer;

	@InjectComponent
	private Zone jobsZone, paginationZone;

    // pagination
    @InjectComponent
    private Pagination pagination;

	// sort
    @Persist(PersistenceConstants.SESSION)
    private List<SolrSortField> sortStack;
	
	
	// Filter
	@Property
	@Persist(PersistenceConstants.SESSION)
	private Integer collectionID;
	
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	private LibraryLocation fLocation;

	
	@SetupRender
    void onSetupRender() {

        if (sortStack == null)
            sortStack = new LinkedList<>();
    }

	
	
	
	
	
	
	
	public SelectModel getLibraryLocationSelectModel() {
		return new LibraryLocationSelectModel(libraryLocationDAO);
	}

	public ValueEncoder<LibraryLocation> getLibraryLocationEncoder() {
		return new LibraryLocationValueEncoder(libraryLocationDAO);
	}

	// Filter changed
	@OnEvent(value = "collectionIDChanged")
	Object onCollectionIDChanged() {
		String param = request.getParameter("param");
		collectionID = NumberUtils.isNumber(param) ? NumberUtils.toInt(param) : null;

		return onFilterChange();
	}

	private Object onFilterChange() {

        pagination.resetCurrentPage();

        if (request.isXHR()) {
            ajaxRenderer.addRender(paginationZone);
        }

        return request.isXHR()
               ? jobsZone.getBody()
               : this;
    }
	
	
    /**
     * Called when the number of results per page is changed in pagination-component
     */
    @OnEvent(component = "pagination", value = "change")
    void onValueChanged() {
        if(request.isXHR())
            ajaxRenderer.addRender(jobsZone)
                    .addRender(paginationZone);
    }

    /**
     * is called when user selects another page in pagination
     */
    void onUpdateZones() {
        if(request.isXHR())
            ajaxRenderer.addRender(jobsZone)
                    .addRender(paginationZone);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	// solr item request
	
	  public SolrResponse getCopyrightReviews() {

	        sortStack = sortStack == null
	                    ? Collections.EMPTY_LIST
	                    : sortStack;
	        try {
	            SolrQueryBuilder queryBuilder = solrService.createQueryBuilder();

	            if (sortStack != null)
	                sortStack.forEach(sortField -> queryBuilder.addSortField(sortField.getFieldName(), sortField.getOrder()));

	            List<SolrQueryField> filter = buildFilterParams();
	            filter.forEach(param -> queryBuilder.and(param));

	            SolrQuery query = queryBuilder.setOffset((pagination.getCurrentPageNumber() - 1) * pagination.getMaxRowsPerPage())
	                    .setCount(pagination.getMaxRowsPerPage())
	                    .build();

	            return solrService.query(SolrCopyrightView.class, query);

	        } catch (SolrServerException e) {
	            log.error("could not query solr server", e);
	            return null;
	        }
	    }
	
	
	
	
	
	
	
//	public SolrResponse getCopyrightReviews() {
//
//		try {
//			// filter by query string
//			SolrQueryBuilder queryBuilder = solrService.createQueryBuilder();
//
//			// if (query != null &&
//			// appliedFilter.contains(CopyrightFilter.QUERY_FILTER)) {
//			// queryBuilder.singleCondition(SolrCopyrightView.SEARCH_FIELD_PROPERTY,
//			// this.query);
//			// }
//			//
//			// // apply select filters
//			// if (mimeFilter != null &&
//			// appliedFilter.contains(CopyrightFilter.MIME_FILTER))
//			// queryBuilder.singleCondition(SolrCopyrightView.MIME_TYPE_PROPERTY,
//			// mimeFilter);
//			// if (reviewStatusFilter != null &&
//			// appliedFilter.contains(CopyrightFilter.STATUS_FILTER))
//			// queryBuilder.singleEqualCondition(SolrCopyrightView.REVIEW_STATUS_PROPERTY,
//			// reviewStatusFilter.getDatabaseValue().toString());
//			//
//			// // apply sort to query
//			// for (SolrSortField field : sortStack) {
//			// SolrQuery.ORDER order = field.getOrder();
//			// if (order != null)
//			// queryBuilder.addSortField(field.getFieldName(), order);
//			// }
//
//			// run query
//			SolrQuery query = queryBuilder
//					.setOffset((pagination.getCurrentPageNumber() - 1) * pagination.getMaxRowsPerPage())
//					.setCount(pagination.getMaxRowsPerPage()).build();
//			SolrResponse<SolrCopyrightView> response = solrService.query(SolrCopyrightView.class, query);
//			log.debug("Response has " + response.getCount() + " items");
//			return response;
//
//		} catch (SolrServerException e) {
//			log.error("could not query solr", e);
//			return new SolrResponse<SolrCopyrightView>();
//
//		}
//
//	}
	
	
    private List<SolrQueryField> buildFilterParams() {
        List<SolrQueryField> result = new ArrayList<>();
        if (collectionID != null) {
            result.add(new SolrNumberQueryField(SolrScanJobView.COLLECTION_ID_PROPERTY, collectionID));
        }
        return result;
    }
	
	

	// solr transferobject

	@Inject
	private Messages messages;

	private static final Format MODIFIED_OUTPUT_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");

	@Inject
	private AssetSource assetSource;

	public String getStatusLabel() {
		return messages.get(CopyrightReviewStatus.getName(review.getReviewStatus()));
	}

	public String getStatusColor() {
		CopyrightReviewStatus reviewStatus = CopyrightReviewStatus.get(review.getReviewStatus());
		return reviewStatus != null ? "#" + reviewStatus.getColor() : "transparent";
	}

	public Format getModifiedOutputFormat() {
		return MODIFIED_OUTPUT_FORMAT;
	}

	public String getMimeTypeIcon() {
		if (review.getMimeType() == null)
			return null;

		String mimeType = review.getMimeType();
		String iconFileName;
		if (mimeType.startsWith("video"))
			iconFileName = "media-video.png";
		else if (mimeType.startsWith("audio"))
			iconFileName = "media-audio.png";
		else if (mimeType.startsWith("image"))
			iconFileName = "media-image.png";
		else if (mimeType.startsWith("text"))
			iconFileName = "text-plain.png";
		else
			iconFileName = "unknown.png";
		Resource asset = assetSource.resourceForPath("context:img/mimetypes/" + iconFileName);
		return asset.getFile();
	}
}
