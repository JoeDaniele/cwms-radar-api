package cwms.radar.api;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.MetricRegistry;
import cwms.radar.formatters.Formats;
import io.javalin.core.util.Header;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled
public class PoolControllerTest extends AbstractControllerTest
{

	@Test
	public void getOneJsonTest() throws SQLException
	{
		final HttpServletRequest request= mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final Map<String, ?> map = new LinkedHashMap<>();

		when(request.getAttribute("office-id")).thenReturn("LRL");
		when(request.getAttribute("database")).thenReturn(getConnection());

		when(request.getHeader(Header.ACCEPT)).thenReturn(Formats.JSONV2);

		Map<String, String> urlParams = new LinkedHashMap<>();
		urlParams.put("office", "LRL");
		urlParams.put("project-id", "WestFork");

		String paramStr = buildParamStr(urlParams);

		when(request.getQueryString()).thenReturn(paramStr);
		when(request.getRequestURL()).thenReturn(new StringBuffer( "http://127.0.0.1:7001/mocked/pools"));

		Context ctx = new Context(request, response, map);

		PoolController controller = new PoolController(new MetricRegistry());

		controller.getOne(ctx, "Normal");

		verify(response).setStatus(200);
		verify(response).setContentType(Formats.JSONV2);

		String result = ctx.resultString();
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void getOneJsonNotPresentTest() throws SQLException
	{
		final HttpServletRequest request= mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final Map<String, ?> map = new LinkedHashMap<>();

		when(request.getAttribute("office-id")).thenReturn("LRL");
		when(request.getAttribute("database")).thenReturn(getConnection());

		when(request.getHeader(Header.ACCEPT)).thenReturn(Formats.JSONV2);

		Map<String, String> urlParams = new LinkedHashMap<>();
		urlParams.put("office", "LRL");
		urlParams.put("project-id", "WestNotARealPlace");

		String paramStr = buildParamStr(urlParams);

		when(request.getQueryString()).thenReturn(paramStr);
		when(request.getRequestURL()).thenReturn(new StringBuffer( "http://127.0.0.1:7001/mocked/pools"));

		Context ctx = new Context(request, response, map);

		PoolController controller = new PoolController(new MetricRegistry());

		controller.getOne(ctx, "Normal");

		verify(response).setStatus(404);
		verify(response).setContentType(Formats.JSON);

		String result = ctx.resultString();
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@NotNull
	private String buildParamStr(Map<String, String> urlParams)
	{
		StringBuilder sb = new StringBuilder();
		urlParams.entrySet().stream().forEach(e->sb.append(e.getKey()).append("=").append(e.getValue()).append("&"));

		if(sb.length() > 0){
			sb.setLength(sb.length()-1);
		}

		String paramStr = sb.toString();
		return paramStr;
	}


	@Test
	public void getAllJsonTest() throws SQLException
	{
		final HttpServletRequest request= mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final Map<String, ?> map = new LinkedHashMap<>();

		when(request.getAttribute("office-id")).thenReturn("LRL");
		when(request.getAttribute("database")).thenReturn(getConnection());

		when(request.getHeader(Header.ACCEPT)).thenReturn(Formats.JSONV2);

		Map<String, String> urlParams = new LinkedHashMap<>();
		urlParams.put("office", "LRL");
//		urlParams.put("project-id", "*");
		urlParams.put("pageSize", "5");

		String paramStr = buildParamStr(urlParams);

		when(request.getQueryString()).thenReturn(paramStr);

		Context ctx = new Context(request, response, map);

		PoolController controller = new PoolController(new MetricRegistry());

		controller.getAll(ctx);

		verify(response).setStatus(200);
		verify(response).setContentType(Formats.JSONV2);

		String result = ctx.resultString();
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}


}