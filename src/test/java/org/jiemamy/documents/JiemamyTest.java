package org.jiemamy.documents;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jiemamy.Jiemamy;
import org.jiemamy.JiemamyFactory;
import org.jiemamy.composer.exporter.DefaultSqlExportConfig;
import org.jiemamy.composer.exporter.SqlExporter;
import org.jiemamy.dialect.BuiltinDataTypeMold;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.mysql.MySqlDialect;
import org.jiemamy.model.RootModel;
import org.jiemamy.model.attribute.ColumnModel;
import org.jiemamy.model.datatype.BuiltinDataType;
import org.jiemamy.model.datatype.DataTypeCategory;
import org.jiemamy.model.datatype.adapter.SizedDataTypeAdapter;
import org.jiemamy.model.entity.TableModel;
import org.jiemamy.serializer.JiemamySerializer;
import org.jiemamy.utils.model.RootModelUtil;
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
	public void test01() throws Exception {
		//@extract-start newJiemamy
		Jiemamy jiemamy = Jiemamy.newInstance();
		//@extract-end newJiemamy
	}
	
	/**
	 * コードサンプル。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test02() throws Exception {
		//@extract-start getFactory
		Jiemamy jiemamy = Jiemamy.newInstance();
		JiemamyFactory factory = jiemamy.getFactory();
		//@extract-end getFactory
		
		//@extract-start newTable
		TableModel tableModel = factory.newModel(TableModel.class);
		//@extract-end newTable
		
		RootModel rootModel = factory.getRootModel();
		RootModelUtil.setDialect(rootModel, MySqlDialect.class);
		Dialect dialect = rootModel.findDialect();
		
		//@extract-start newDataType
		BuiltinDataTypeMold mold = dialect.findDataTypeMold(DataTypeCategory.VARCHAR);
		BuiltinDataType dataType = factory.newDataType(mold);
		dataType.getAdapter(SizedDataTypeAdapter.class).setSize(36);
		//@extract-end newDataType
	}
	
	/**
	 * コードサンプル。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test03() throws Exception {
		//@extract-start getRootModel
		Jiemamy jiemamy = Jiemamy.newInstance();
		JiemamyFactory factory = jiemamy.getFactory();
		RootModel rootModel = factory.getRootModel();
		//@extract-end getRootModel
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
		Jiemamy jiemamy = Jiemamy.newInstance();
		JiemamyFactory factory = jiemamy.getFactory();
		RootModel rootModel = factory.getRootModel();
		
		// set Dialect to ues and get instance
		RootModelUtil.setDialect(rootModel, MySqlDialect.class);
		Dialect dialect = rootModel.findDialect();
		
		// create TABLE and set name
		TableModel tableModel = factory.newModel(TableModel.class);
		tableModel.setName("T_USER");
		
		// create COLUMN and set name
		ColumnModel columnId = factory.newModel(ColumnModel.class);
		columnId.setName("ID");
		
		// create DataType of INTEGER and set it to column
		BuiltinDataTypeMold mold1 = dialect.findDataTypeMold(DataTypeCategory.INTEGER);
		BuiltinDataType dataType1 = factory.newDataType(mold1);
		columnId.setDataType(dataType1);
		
		// add COLUMN to TABLE
		tableModel.getAttributes().add(columnId);
		
		// create COLUMN and set name
		ColumnModel columnName = factory.newModel(ColumnModel.class);
		columnName.setName("NAME");
		
		// create DataType of VARCHAR(32) and set it to column
		BuiltinDataTypeMold mold2 = dialect.findDataTypeMold(DataTypeCategory.VARCHAR);
		BuiltinDataType dataType2 = factory.newDataType(mold2);
		dataType2.getAdapter(SizedDataTypeAdapter.class).setSize(36);
		columnName.setDataType(dataType2);
		
		// add COLUMN to TABLE
		tableModel.getAttributes().add(columnName);
		
		// add TABLE to RootModel
		rootModel.getEntities().add(tableModel);
		//@extract-end simpleTable
		
		//@extract-start sqlExport
		SqlExporter exporter = new SqlExporter();
		DefaultSqlExportConfig config = new DefaultSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitDropStatements(false);
		config.setOutputFile(new File("./target/test.sql"));
		config.setOverwrite(true);
		boolean result = exporter.exportModel(rootModel, config);
		//@extract-end sqlExport
		
		assertThat(result, is(true));
		
		{
			//@extract-start serialize
			JiemamySerializer serializer = jiemamy.getSerializer();
			serializer.serialize(rootModel, new FileOutputStream("./target/output.jer"));
			//@extract-end serialize
			assertThat(new File("./target/output.jer").exists(), is(true));
		}
		
		new File("./target/output.jer").renameTo(new File("output.jer"));
		
		{
			//@extract-start deserialize
			JiemamySerializer serializer = jiemamy.getSerializer();
			RootModel deserialized = serializer.deserialize(new FileInputStream("./target/output.jer"));
			//@extract-end deserialize
			assertThat(deserialized.getEntities().size(), is(1));
		}
	}
	
}
