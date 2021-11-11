package cwms.radar.data.dao;

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.Statement;
//import oracle.jdbc.internal.OracleResultSet;
//import oracle.sql.BLOB;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import cwms.radar.data.dto.Blob;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class BlobDaoTest extends DaoTest
{
	@Disabled
	@Test
	public void testGetOne() throws SQLException
	{
		try (DSLContext dsl = getDslContext(getConnection(), "LRL"))
		{
			BlobDao dao = new BlobDao(dsl);

			Blob blob = dao.getByUniqueName("image20210813", Optional.of("LRL")).get();
			assertNotNull(blob);
		}

	}

	@Test
	public void testGetAll() throws SQLException
	{
		try (DSLContext dsl = getDslContext(getConnection(), "LRL"))
		{
			BlobDao dao = new BlobDao(dsl);

			List<Blob> blobs = dao.getAll(Optional.of("LRL"));
			assertNotNull(blobs);
			assertFalse(blobs.size() <=1);
		}

	}

//	public void testInsert() throws SQLException, IOException
//	{
//		try (
//		Connection connection = getConnection())
//		{
////			try (DSLContext dsl = getDslContext(connection, "LRL")){
//
//			// Trying to follow https://docs.oracle.com/cd/F49540_01/DOC/java.815/a64685/oraext4.htm
//
//			// Step 1
//
//			Statement stmt = connection.createStatement();
//			// select * from CWMS_MEDIA_TYPE where CWMS_MEDIA_TYPE.MEDIA_TYPE_CLOB_TF='F' and MEDIA_TYPE_ID like 'image/%'
//			// 1235 looks like image/png
//
//			int officeCode = 9;
//			String blobId = "image20210813";
//			String blobDesc = "image to test blob insert";
//			int mediaCode = 1235;
////			String query = String.format(
////					"insert into CWMS_20.AT_BLOB(BLOB_CODE, OFFICE_CODE, ID, DESCRIPTION, MEDIA_TYPE_CODE, VALUE) " +
////							"values (123123, %d, '%s', '%s', %d,  empty_blob())",
////					officeCode, blobId, blobDesc, mediaCode);
////			boolean execute = stmt.execute(query);
//
//			// Step 2
//			String cmd = String.format("SELECT OFFICE_CODE, ID, VALUE FROM CWMS_20.AT_BLOB WHERE ID='%s' and OFFICE_CODE=%d FOR UPDATE", blobId, officeCode);
//			ResultSet rset = stmt.executeQuery(cmd);
//			rset.next();
//			ResultSetMetaData metaData = rset.getMetaData();
//			int columnCount = metaData.getColumnCount();
//			System.out.println("column count " + columnCount);
//			for(int i = 1; i <= columnCount; i++)
//			{
//				System.out.println("column " + i + " label:" + metaData.getColumnLabel(i));
//
//			}
//			BLOB blob = ((OracleResultSet)rset).getBLOB(3);
//
//			// Step 3
//			try(InputStream inputStream = BlobDaoTest.class.getResourceAsStream(
//					"/cwms/radar/data/dao/usace.png"))
//			{
//				writeFileIntoBlob(blob, inputStream);
//			}
//		}
//
//	}
//
//	public void testInsert2() throws SQLException, IOException
//	{
//		try (
//				Connection connection = getConnection())
//		{
//			//			try (DSLContext dsl = getDslContext(connection, "LRL")){
//
//			// Trying to follow https://docs.oracle.com/cd/F49540_01/DOC/java.815/a64685/oraext4.htm
//
//			// Step 1
//
//			Statement stmt = connection.createStatement();
//			// select * from CWMS_MEDIA_TYPE where CWMS_MEDIA_TYPE.MEDIA_TYPE_CLOB_TF='F' and MEDIA_TYPE_ID like 'image/%'
//			// 1230 looks like image/jpeg
//
//			int officeCode = 9;
//			String blobId = "keystone20210813";
//			String blobDesc = "2md image to test blob insert";
//			int mediaCode = 1230;
//			int blobCode = 123124;
//			String query = String.format(
//								"insert into CWMS_20.AT_BLOB(BLOB_CODE, OFFICE_CODE, ID, DESCRIPTION, MEDIA_TYPE_CODE, VALUE) " +
//										"values (" + blobCode +
//										", %d, '%s', '%s', %d,  empty_blob())",
//								officeCode, blobId, blobDesc, mediaCode);
//						boolean execute = stmt.execute(query);
//
//			// Step 2
//			String cmd = String.format("SELECT OFFICE_CODE, ID, VALUE FROM CWMS_20.AT_BLOB WHERE ID='%s' and OFFICE_CODE=%d FOR UPDATE", blobId, officeCode);
//			ResultSet rset = stmt.executeQuery(cmd);
//			rset.next();
//			ResultSetMetaData metaData = rset.getMetaData();
//			int columnCount = metaData.getColumnCount();
//			System.out.println("column count " + columnCount);
//			for(int i = 1; i <= columnCount; i++)
//			{
//				System.out.println("column " + i + " label:" + metaData.getColumnLabel(i));
//
//			}
//			BLOB blob = ((OracleResultSet)rset).getBLOB(3);
//
//			// Step 3
//			try(InputStream inputStream = BlobDaoTest.class.getResourceAsStream(
//					"/cwms/radar/data/dao/keystone23may20191.jpg"))
//			{
//				writeFileIntoBlob(blob, inputStream);
//			}
//		}
//
//	}
//
//	private void writeFileIntoBlob(BLOB blob, String filepath) throws SQLException, IOException
//	{
//		File binaryFile = new File(filepath);
//		System.out.println("file length = " + binaryFile.length());
//		FileInputStream instream = new FileInputStream(binaryFile);
//		writeStreamIntoBlob(blob, instream);
//		instream.close();
//	}
//
//	private void writeStreamIntoBlob(BLOB blob, InputStream instream) throws IOException
//	{
//		OutputStream outstream = blob.getBinaryOutputStream();
//
//		// Step 4
//		int chunk = blob.getChunkSize();
//		System.out.println("chunksize was " + chunk);
//		byte[] buffer = new byte[chunk];
//		int length = -1;
//
//		// Step 5
//		while((length = instream.read(buffer)) != -1)
//			outstream.write(buffer, 0, length);
//		outstream.close();
//	}


}