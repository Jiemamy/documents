package org.jiemamy.documents;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SimpleJmMetadata;
import org.jiemamy.composer.exporter.SimpleSqlExportConfig;
import org.jiemamy.composer.exporter.SqlExporter;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.mysql.MySqlDialect;
import org.jiemamy.model.column.SimpleJmColumn;
import org.jiemamy.model.datatype.RawTypeCategory;
import org.jiemamy.model.datatype.RawTypeDescriptor;
import org.jiemamy.model.datatype.SimpleDataType;
import org.jiemamy.model.datatype.SimpleRawTypeDescriptor;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.table.SimpleJmTable;
import org.jiemamy.serializer.JiemamySerializer;
import org.junit.Test;

/**
 * コードサンプル。
 * 
 * @author daisuke
 */
public class JiemamyTest {

	/**
	 * コードサンプル。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	@SuppressWarnings("unused")
	public void test01() throws Exception {
		//@extract-start newJiemamy
		JiemamyContext context = new JiemamyContext();
		//@extract-end newJiemamy
	}
	
	/**
	 * コードサンプル。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	@SuppressWarnings("unused")
	public void test02() throws Exception {
		//@extract-start newTable
		SimpleJmTable table = new SimpleJmTable();
		//@extract-end newTable
		
		JiemamyContext context = new JiemamyContext();
		SimpleJmMetadata metadata = new SimpleJmMetadata();
		metadata.setDialectClassName(MySqlDialect.class.getName());
		context.setMetadata(metadata);
		Dialect dialect = context.findDialect();
		
		//@extract-start newDataType
		SimpleRawTypeDescriptor typeDesc = new SimpleRawTypeDescriptor(RawTypeCategory.VARCHAR);
		SimpleDataType dataType = new SimpleDataType(typeDesc);
		dataType.putParam(TypeParameterKey.SIZE, 36);
		//@extract-end newDataType
	}
	
	/**
	 * コードサンプル。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test04() throws Exception {
		//@extract-start simpleTable
		// initialize Jiemamy
		JiemamyContext context = new JiemamyContext();
		
		// set Dialect to ues and get instance
		SimpleJmMetadata metadata = new SimpleJmMetadata();
		metadata.setDialectClassName(MySqlDialect.class.getName());
		context.setMetadata(metadata);
		Dialect dialect = context.findDialect();
		
		// create TABLE and set name
		SimpleJmTable table = new SimpleJmTable();
		table.setName("T_USER");
		
		// create COLUMN and set name
		SimpleJmColumn columnId = new SimpleJmColumn();
		columnId.setName("ID");
		
		// create DataType of INTEGER and set it to column
		RawTypeDescriptor integer = dialect.normalize(new SimpleRawTypeDescriptor(RawTypeCategory.INTEGER));
		SimpleDataType type1 = new SimpleDataType(integer);
		type1.putParam(TypeParameterKey.SERIAL, true);
		columnId.setDataType(type1);
		
		// add COLUMN to TABLE
		table.store(columnId);
		
		// create COLUMN and set name
		SimpleJmColumn columnName = new SimpleJmColumn();
		columnName.setName("NAME");
		
		// create DataType of VARCHAR(32) and set it to column
		RawTypeDescriptor varchar = dialect.normalize(new SimpleRawTypeDescriptor(RawTypeCategory.VARCHAR));
		SimpleDataType type2 = new SimpleDataType(varchar);
		type2.putParam(TypeParameterKey.SIZE, 36);
		columnName.setDataType(type2);
		
		// add COLUMN to TABLE
		table.store(columnName);
		
		// add TABLE to JiemamyContext
		context.store(table);
		//@extract-end simpleTable
		
		//@extract-start sqlExport
		SqlExporter exporter = new SqlExporter();
		SimpleSqlExportConfig config = new SimpleSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitDropStatements(false);
		config.setOutputFile(new File("./target/test.sql"));
		config.setOverwrite(true);
		boolean result = exporter.exportModel(context, config);
		//@extract-end sqlExport
		
		assertThat(result, is(true));
		
		{
			//@extract-start serialize
			JiemamySerializer serializer = JiemamyContext.findSerializer();
			serializer.serialize(context, new FileOutputStream("./target/output.jer"));
			//@extract-end serialize
			assertThat(new File("./target/output.jer").exists(), is(true));
		}
		
		new File("./target/output.jer").renameTo(new File("output.jer"));
		
		{
			//@extract-start deserialize
			JiemamySerializer serializer = JiemamyContext.findSerializer();
			JiemamyContext deserialized = serializer.deserialize(new FileInputStream("./target/output.jer"));
			//@extract-end deserialize
			assertThat(deserialized.getTables().size(), is(1));
		}
	}
	
}
