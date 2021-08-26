package cwms.radar.api;

import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import cwms.radar.api.errors.RadarError;
import cwms.radar.data.dao.PoolDao;
import cwms.radar.data.dto.Pool;
import cwms.radar.data.dto.Pools;
import cwms.radar.formatters.ContentType;
import cwms.radar.formatters.Formats;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.core.util.Header;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.javalin.plugin.openapi.annotations.OpenApiSecurity;
import org.eclipse.jetty.http.HttpStatus;
import org.jooq.DSLContext;

import static com.codahale.metrics.MetricRegistry.name;
import static cwms.radar.data.dao.JooqDao.getDslContext;

public class PoolController implements CrudHandler
{
	public static final Logger logger = Logger.getLogger(PoolController.class.getName());
	public static final String ANY_MASK = "*";
	private static final int defaultPageSize = 100;

	private final MetricRegistry metrics;
	private final Meter getAllRequests;
	private final Timer getAllRequestsTime;
	private final Meter getOneRequest;
	private final Timer getOneRequestTime;
	private final Histogram requestResultSize;

	public PoolController(MetricRegistry metrics)
	{
		this.metrics = metrics;
		String className = this.getClass().getName();
		getAllRequests = this.metrics.meter(name(className, "getAll", "count"));
		getAllRequestsTime = this.metrics.timer(name(className, "getAll", "time"));
		getOneRequest = this.metrics.meter(name(className, "getOne", "count"));
		getOneRequestTime = this.metrics.timer(name(className, "getOne", "time"));
		requestResultSize = this.metrics.histogram((name(className, "results", "size")));
	}

	@OpenApi(queryParams = {
			@OpenApiParam(name = "office", description = "Specifies the owning office of the data in the response. If this field is not specified, matching items from all offices shall be returned."),
			@OpenApiParam(name = "id-mask", description = "Project Id mask. Default value:*"),
			@OpenApiParam(name = "name-mask", description = "Name mask. Default value:*\""),
			@OpenApiParam(name = "bottom-mask", description = "Bottom level mask. Default value:*\""),
			@OpenApiParam(name = "top-mask", description = "Top level mask. Default value:*\""),
			@OpenApiParam(name = "include-explicit", description = "Specifies if the results should include explicit Pools. Default value:false\""),
			@OpenApiParam(name = "include-implicit", description = "Specifies if the results should include implicit Pools..Default value:true\""),
			@OpenApiParam(name="page",
					required = false,
					description = "This end point can return a lot of data, this identifies where in the request you are. This is an opaque value, and can be obtained from the 'next-page' value in the response."
			),
			@OpenApiParam(name="pageSize",
					required=false,
					type=Integer.class,
					description = "How many entries per page returned. Default " + defaultPageSize + "."
			),
	}, responses = {
			@OpenApiResponse(status = "200", content = {
					@OpenApiContent(type = Formats.JSONV2, from = Pools.class)}),
			@OpenApiResponse(status = "404", description = "Based on the combination of inputs provided the pools were not found."),
			@OpenApiResponse(status = "501", description = "request format is not implemented")
	},
			description = "Returns Pools Data",
			tags = {"Pools"})
	@Override
	public void getAll(Context ctx)
	{
		getAllRequests.mark();
		try(final Timer.Context timeContext = getAllRequestsTime.time();
			DSLContext dsl = getDslContext(ctx))
		{
			PoolDao dao = new PoolDao(dsl);
			String office = ctx.queryParam("office");

			String projectIdMask = ctx.queryParam("id-mask", ANY_MASK);
			String nameMask = ctx.queryParam("name-mask", ANY_MASK)	;
			String bottomMask = ctx.queryParam("bottom-mask", ANY_MASK);
			String topMask = ctx.queryParam("top-mask", ANY_MASK);

			String isExp = ctx.queryParam("include-explicit", "false");
			boolean isExplicit = Boolean.parseBoolean(isExp);
			String isImp = ctx.queryParam("include-implicit", "true");
			boolean isImplicit = Boolean.parseBoolean(isImp);

			String cursor = ctx.queryParam("cursor",String.class,ctx.queryParam("page",String.class,"").getValue()).getValue();
			int pageSize = ctx.queryParam("pageSize",Integer.class,ctx.queryParam("pagesize",String.class,Integer.toString(defaultPageSize)).getValue()).getValue();

			Pools pools = dao.retrievePools(cursor, pageSize, projectIdMask, nameMask, bottomMask, topMask, isExplicit, isImplicit, office);

			String formatHeader = ctx.header(Header.ACCEPT);
			ContentType contentType = Formats.parseHeaderAndQueryParm(formatHeader, "");

			String result = Formats.format(contentType,pools);

			ctx.result(result).contentType(contentType.toString());
			requestResultSize.update(result.length());

			ctx.status(HttpServletResponse.SC_OK);
		}

	}

	@OpenApi(
			pathParams = {
					@OpenApiParam(name = "pool-id", required = true, description = "Specifies the pool whose data is to be included in the response."),
			},
			queryParams = {
					@OpenApiParam(name = "office", required = true, description = "Specifies the owning office of the Pool whose data is to be included in the response."),
					@OpenApiParam(name = "project-id", required = true, description = "Specifies the project-id of the Pool whose data is to be included in the response."),
					@OpenApiParam(name = "bottom-mask", description = "Bottom level mask. Default value:*\""),
					@OpenApiParam(name = "top-mask", description = "Top level mask. Default value:*\""),
					@OpenApiParam(name = "include-explicit", description = "Specifies if the results should include explicit Pools. Default value:false\""),
					@OpenApiParam(name = "include-implicit", description = "Specifies if the results should include implicit Pools..Default value:true\""),

			},
			responses = {
					@OpenApiResponse(status = "200",
							content = {
									@OpenApiContent(from = Pool.class, type = Formats.JSONV2)
							}
					),
					@OpenApiResponse(status = "404", description = "Based on the combination of inputs provided the Location Category was not found."),
					@OpenApiResponse(status = "501", description = "request format is not implemented")},
			description = "Retrieves requested Pool", tags = {"Pools"},
			security = {@OpenApiSecurity(name = "bearerAuth")
	}
	)
	@Override
	public void getOne(Context ctx, String poolId)
	{
		getOneRequest.mark();
		try(final Timer.Context timeContext = getOneRequestTime.time(); DSLContext dsl = getDslContext(ctx))
		{
			PoolDao dao = new PoolDao(dsl);

			// These are required
			String office = ctx.queryParam("office");
			String projectId = ctx.queryParam("project-id");

			// These are optional
			String bottomMask = ctx.queryParam("bottom-mask", ANY_MASK);
			String topMask = ctx.queryParam("top-mask", ANY_MASK);
			String isExp = ctx.queryParam("include-explicit", "true");
			boolean isExplicit = Boolean.parseBoolean(isExp);
			String isImp = ctx.queryParam("include-implicit", "true");
			boolean isImplicit = Boolean.parseBoolean(isImp);

			// I want to call retrievePool but it doesn't return implicit pools
			//			pool = dao.retrievePool(projectId, poolId, office);
			Pool pool = dao.retrievePoolFromCatalog(projectId, poolId, bottomMask, topMask, isExplicit, isImplicit,
					office);
			if(pool != null)
			{
				String formatHeader = ctx.header(Header.ACCEPT);
				ContentType contentType = Formats.parseHeaderAndQueryParm(formatHeader, "");
				ctx.contentType(contentType.toString());

				String result = Formats.format(contentType, pool);

				ctx.result(result);
				requestResultSize.update(result.length());

				ctx.status(HttpServletResponse.SC_OK);
			}
			else
			{
				ctx.status(HttpStatus.NOT_FOUND_404).json(
						new RadarError("Unable to find pool based on given parameters"));
			}
		}

	}

	@OpenApi(ignore = true)
	@Override
	public void create(Context ctx)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@OpenApi(ignore = true)
	@Override
	public void update(Context ctx, String locationCode)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@OpenApi(ignore = true)
	@Override
	public void delete(Context ctx, String locationCode)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
