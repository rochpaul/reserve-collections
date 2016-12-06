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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientBodyElement;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.EventLink;
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
import unidue.rc.search.SolrTextQueryField;
import unidue.rc.ui.ProtectedPage;
import unidue.rc.ui.components.AjaxSortLink;
import unidue.rc.ui.components.AjaxSortLink.SortState;
import unidue.rc.ui.components.Pagination;
import unidue.rc.ui.selectmodel.LibraryLocationSelectModel;
import unidue.rc.ui.valueencoder.LibraryLocationValueEncoder;

@ProtectedPage
public class CopyrightReviewJobs {

	private enum BlockDefinition {
		EditJob
	}

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
	private Zone editJobZone, filterZone, jobsZone, paginationZone;

	// pagination
	@InjectComponent
	private Pagination pagination;

	// sort
	@Persist(PersistenceConstants.SESSION)
	private List<SolrSortField> sortStack;

	// Filter
	@Property
	@Persist(PersistenceConstants.SESSION)
	private Integer filterCollectionID;

	@Property
	@Persist(PersistenceConstants.SESSION)
	private String filterFilename;

	@Property
	@Persist(PersistenceConstants.SESSION)
	private LibraryLocation filterLocation;

	@Property(write = false)
	@Inject
	private Block editJobBlock;

	@InjectComponent("editJobLink")
	@Property(write = false)
	private EventLink editJobLink;

	@SetupRender
	void onSetupRender() {

		if (sortStack == null)
			sortStack = new LinkedList<>();
	}

	// edit part
	@OnEvent(value = "editJob")
	void onEditJob(int collectionID) {
		// try {

		// loadEditJobData(scanJobID);
		visibleBlock = BlockDefinition.EditJob;
		addAjaxRender(editJobZone);

		// } catch (SolrServerException e) {
		// log.error("could not get scan job from solr", e);
		// }
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
		filterCollectionID = NumberUtils.isNumber(param) ? NumberUtils.toInt(param) : null;

		return onFilterChange();
	}

	@OnEvent(value = "filterFilenameChanged")
	Object onValueChangedFromAuthor() {
		filterFilename = request.getParameter("param");

		return onFilterChange();
	}

	@OnEvent(value = EventConstants.VALUE_CHANGED, component = "locationFilter")
	Object onValueChangedFromLocationFilter(LibraryLocation location) {
		filterLocation = location;

		return onFilterChange();
	}

	private Object onFilterChange() {

		pagination.resetCurrentPage();

		if (request.isXHR()) {
			ajaxRenderer.addRender(paginationZone);
		}

		return request.isXHR() ? jobsZone.getBody() : this;
	}

	/**
	 * Called when the number of results per page is changed in
	 * pagination-component
	 */
	@OnEvent(component = "pagination", value = "change")
	void onValueChanged() {
		if (request.isXHR())
			ajaxRenderer.addRender(jobsZone).addRender(paginationZone);
	}

	/**
	 * is called when user selects another page in pagination
	 */
	void onUpdateZones() {
		if (request.isXHR())
			ajaxRenderer.addRender(jobsZone).addRender(paginationZone);
	}

	// solr item request

	public SolrResponse<SolrCopyrightView> getCopyrightReviews() {

		sortStack = sortStack == null ? Collections.EMPTY_LIST : sortStack;
		try {
			SolrQueryBuilder queryBuilder = solrService.createQueryBuilder();

			if (sortStack != null)
				sortStack.forEach(
						sortField -> queryBuilder.addSortField(sortField.getFieldName(), sortField.getOrder()));

			List<SolrQueryField> filter = buildFilterParams();
			filter.forEach(param -> queryBuilder.and(param));

			SolrQuery query = queryBuilder
					.setOffset((pagination.getCurrentPageNumber() - 1) * pagination.getMaxRowsPerPage())
					.setCount(pagination.getMaxRowsPerPage()).build();

			SolrResponse<SolrCopyrightView> response = solrService.query(SolrCopyrightView.class, query);

			return response;

		} catch (SolrServerException e) {
			log.error("could not query solr server", e);
			return null;
		}
	}

	private List<SolrQueryField> buildFilterParams() {
		List<SolrQueryField> result = new ArrayList<>();

		if (StringUtils.isNotBlank(filterFilename)) {
			result.add(new SolrTextQueryField(SolrCopyrightView.FILE_NAME_PROPERTY, filterFilename));
		}

		if (filterCollectionID != null) {
			result.add(new SolrNumberQueryField(SolrCopyrightView.COLLECTION_ID_PROPERTY, filterCollectionID));
		}

		if (filterLocation != null) {
			result.add(new SolrNumberQueryField(SolrScanJobView.LOCATION_ID_PROPERTY, filterLocation.getId()));
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

	// ajax sort
	@OnEvent(value = "sort")
	void onSort(String column, AjaxSortLink.SortState newSortState) {

		SolrQuery.ORDER solrOrder = getSolrOrder(newSortState);
		Optional<SolrSortField> sortItem = sortStack.stream().filter(item -> item.getFieldName().equals(column))
				.findAny();

		// if the user has already sorted after column
		if (sortItem.isPresent()) {
			SolrSortField sortField = sortItem.get();
			// apply next order if given
			if (solrOrder != null)
				sortField.setOrder(solrOrder);

			// otherwise remove ordering (do not just set to null!)
			else
				sortStack.remove(sortField);
		}

		// else if sort is asc or desc add the field
		else if (solrOrder != null) {
			SolrSortField sortField = new SolrSortField(column);
			sortField.setOrder(solrOrder);
			sortStack.add(sortField);
		}

		addAjaxRender(jobsZone, filterZone);
	}

	private static SolrQuery.ORDER getSolrOrder(SortState sortState) {
		switch (sortState) {
		case Ascending:
			return SolrQuery.ORDER.asc;
		case Descending:
			return SolrQuery.ORDER.desc;
		case NoSort:
		default:
			return null;
		}
	}

	@Property(write = false)
	@Persist(PersistenceConstants.FLASH)
	private BlockDefinition visibleBlock;

	private void addAjaxRender(ClientBodyElement... elements) {
		if (request.isXHR())
			Arrays.stream(elements).forEach(e -> ajaxRenderer.addRender(e));
	}

	public boolean isBlockVisible(String name) {
		Optional<BlockDefinition> block = Arrays.stream(BlockDefinition.values()).filter(d -> d.name().equals(name))
				.findFirst();
		return block.isPresent() && block.get().equals(visibleBlock);
	}

}
