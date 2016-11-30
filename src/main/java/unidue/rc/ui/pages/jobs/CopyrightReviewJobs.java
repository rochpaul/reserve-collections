package unidue.rc.ui.pages.jobs;

import java.text.Format;
import java.text.SimpleDateFormat;

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
import unidue.rc.search.SolrQueryBuilder;
import unidue.rc.search.SolrResponse;
import unidue.rc.search.SolrService;
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

	// Filter
	@Property
	@Persist(PersistenceConstants.SESSION)
	private LibraryLocation fLocation;

	@Property
	@Persist(PersistenceConstants.SESSION)
	private Integer fNumber;

	public SelectModel getLibraryLocationSelectModel() {
		return new LibraryLocationSelectModel(libraryLocationDAO);
	}

	public ValueEncoder<LibraryLocation> getLibraryLocationEncoder() {
		return new LibraryLocationValueEncoder(libraryLocationDAO);
	}

	// Filter changed
	@OnEvent(value = "filterNumberChanged")
	Object onFilterNumberChanged() {
		String param = request.getParameter("param");
		fNumber = NumberUtils.isNumber(param) ? NumberUtils.toInt(param) : null;

		return onFilterChange();
	}

	private Object onFilterChange() {

		pagination.resetCurrentPage();

		if (request.isXHR()) {
			ajaxRenderer.addRender(paginationZone);
		}

		return request.isXHR() ? jobsZone.getBody() : this;
	}

	public SolrResponse getCopyrightReviews() {

		try {
			// filter by query string
			SolrQueryBuilder queryBuilder = solrService.createQueryBuilder();

			// if (query != null &&
			// appliedFilter.contains(CopyrightFilter.QUERY_FILTER)) {
			// queryBuilder.singleCondition(SolrCopyrightView.SEARCH_FIELD_PROPERTY,
			// this.query);
			// }
			//
			// // apply select filters
			// if (mimeFilter != null &&
			// appliedFilter.contains(CopyrightFilter.MIME_FILTER))
			// queryBuilder.singleCondition(SolrCopyrightView.MIME_TYPE_PROPERTY,
			// mimeFilter);
			// if (reviewStatusFilter != null &&
			// appliedFilter.contains(CopyrightFilter.STATUS_FILTER))
			// queryBuilder.singleEqualCondition(SolrCopyrightView.REVIEW_STATUS_PROPERTY,
			// reviewStatusFilter.getDatabaseValue().toString());
			//
			// // apply sort to query
			// for (SolrSortField field : sortStack) {
			// SolrQuery.ORDER order = field.getOrder();
			// if (order != null)
			// queryBuilder.addSortField(field.getFieldName(), order);
			// }

			// run query
			SolrQuery query = queryBuilder
					.setOffset((pagination.getCurrentPageNumber() - 1) * pagination.getMaxRowsPerPage())
					.setCount(pagination.getMaxRowsPerPage()).build();
			SolrResponse<SolrCopyrightView> response = solrService.query(SolrCopyrightView.class, query);
			log.debug("Response has " + response.getCount() + " items");
			return response;

		} catch (SolrServerException e) {
			log.error("could not query solr", e);
			return new SolrResponse<SolrCopyrightView>();

		}

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
