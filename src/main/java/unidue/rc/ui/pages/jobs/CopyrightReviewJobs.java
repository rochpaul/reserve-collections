package unidue.rc.ui.pages.jobs;

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
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import unidue.rc.dao.LibraryLocationDAO;
import unidue.rc.model.LibraryLocation;
import unidue.rc.model.solr.SolrCopyrightView;
import unidue.rc.search.SolrQueryBuilder;
import unidue.rc.search.SolrResponse;
import unidue.rc.search.SolrSortField;
import unidue.rc.ui.ProtectedPage;
import unidue.rc.ui.components.Pagination;
import unidue.rc.ui.pages.jobs.CopyrightReviewJobsOriginal.CopyrightFilter;
import unidue.rc.ui.selectmodel.LibraryLocationSelectModel;
import unidue.rc.ui.valueencoder.LibraryLocationValueEncoder;

@ProtectedPage
public class CopyrightReviewJobs {

//    private enum BlockDefinition {
//        Batch, EditJob
//    }
    
    @Inject
    private LibraryLocationDAO libraryLocationDAO;
    
    @Inject
    private Request request;
    
    @Inject
    private AjaxResponseRenderer ajaxRenderer;
	
    @InjectComponent
    private Zone jobsZone, paginationZone;
    
    
    @Property
    private SolrCopyrightView review;
    
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
        fNumber = NumberUtils.isNumber(param)
                  ? NumberUtils.toInt(param)
                  : null;

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
    
    
    
    
    
    
    
    public SolrResponse getCopyrightReviews() {
       
            return new SolrResponse<SolrCopyrightView>();
        }
    
}

