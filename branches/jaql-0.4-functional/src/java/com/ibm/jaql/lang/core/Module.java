/*
 * Copyright (C) IBM Corp. 2008.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.jaql.lang.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.ibm.jaql.json.parser.JsonParser;
import com.ibm.jaql.json.parser.ParseException;
import com.ibm.jaql.json.schema.Schema;
import com.ibm.jaql.json.schema.SchemaFactory;
import com.ibm.jaql.json.type.BufferedJsonRecord;
import com.ibm.jaql.json.type.JsonArray;
import com.ibm.jaql.json.type.JsonRecord;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.util.JaqlUtil;

public abstract class Module {
	protected static final String JAQL_DIRECTORY = "scripts";
	protected static final String JAR_DIRECTORY = "jars";
	protected static final String EXAMPLE_DIRECTORY = "examples";
	protected static final String TEST_DIRECTORY = "tests";
	protected static final String NAMESPACE_FILE = "namespace.json";
	protected static final String MODULE_FILE = "description.json";
	protected static final JsonString UDF_FN_FIELD = new JsonString("functions");
	protected static final JsonString JAQL_FIELD = new JsonString("scripts");
	protected static final JsonString EXPORT_FIELD = new JsonString("export");
	protected static final JsonString VERSION_FIELD = new JsonString("version");
	protected static final JsonString IMPORT_FIELD = new JsonString("import");
	protected static String[] searchPath = null;
	protected static Schema namespaceSchema = null;
	protected static Schema descriptionSchema = null;
	
	public static void initSchemas() {
		try {
			Module.namespaceSchema = SchemaFactory.parse( 
					"{" +
					VERSION_FIELD + ": decfloat(value=0.1m)," + "\n" +
					JAQL_FIELD + "?: [ string<0,*> ]," +"\n" +
					EXPORT_FIELD + "?: [ string<0,*> ]," +"\n" +
					UDF_FN_FIELD + "?: [ { \"type\"?: string, * }<0,*> ], " + "\n" +
					IMPORT_FIELD + "?: [ {\"module\": string, \"vars\": [ string<0,*> ] }|string<0,*> ] " + "\n" +
			    "}");
			
			Module.descriptionSchema = SchemaFactory.parse( 
					"{" +
					VERSION_FIELD + "?: decfloat," +
					"date?: date," +
					"title?: string," +
					"author?: string | [ string<0,*> ]," +
					"maintainer?: string | [ string<0,*> ]," +
					"description?: string," +
					"license?: string," +
					"url?: string" +
					"}");
		} catch (IOException e) {
		  throw JaqlUtil.rethrow(e);
		}
	}
	
	public static void setSearchPath(String[] searchPath) {
		Module.searchPath = searchPath; 
	}	
	
	public static Module findModule(String name) {
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File dir) {
				if(dir.isDirectory()) {
					return isModuleDirectory(dir);
				}
				return false;
			}
		};
		
		for (String dir : searchPath) {
			File d = new File(dir);
			if(!d.isDirectory()) {
				throw new RuntimeException(d.getAbsolutePath() + " is no directory");
			}
			if(isModuleDirectory(d)) {
				if(dir.equals(name)) {
					return new DefaultModule(d);
				}
			}
			else {
				File[] modules = d.listFiles(filter);
				for (File module : modules) {
					if(name.equals(module.getName())) {
						return new DefaultModule(module);
					}
				}
			}
		}
		
		throw new RuntimeException("Could not find module " + name);
	}
	
	public static boolean isModuleDirectory(final File f) {
		if(!f.isDirectory()) {
			return false;
		}
		
		FilenameFilter moduleFilter = new FilenameFilter() {
			@Override
			public boolean accept(File directory, String entry) {
				if(JAQL_DIRECTORY.equals(entry)) {
					return true;
				}
				if(NAMESPACE_FILE.equals(entry)) {
					return true;
				}
				return false;
			}
		};
		
		if(f.list(moduleFilter).length > 0) {
			return true;
		}
		
		return false;
	}
	
	protected JsonRecord parseMetaData(InputStream in) {
		JsonRecord meta = new BufferedJsonRecord();
  	
		JsonParser p = new JsonParser(in);
  	JsonValue data = null;
		try {
			data = p.JsonVal();
			//if(p.JsonVal() != null) {
			//	throw new RuntimeException("Only one record allowed in namespace file");
			//}
		} catch (ParseException e) {
			throw new RuntimeException("Error in metadata file", e);
		}
  	if(data != null) {
  		meta = (JsonRecord) data;
  		//Do schema check
			try {
				if(!Module.namespaceSchema.matches(meta)) {
					//Do some minimal analysis to see whats wrong.
					if(!meta.containsKey(VERSION_FIELD)) {
						throw new RuntimeException("Namespace files is missing version");
					} else {
						throw new RuntimeException("Namespace file does not match schema");
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
  	} 
  	
  	return meta;
	}
	
	protected JsonRecord meta = null;
	
	protected void ensureMetaData(){
		initSchemas();
		if(meta != null) {
			return;
		}
		
		meta = parseMetaData(getMetaDataStream());
	}
	

	public JsonRecord getNamespaceDescription() {
		ensureMetaData();
		return meta;
	}
	

	public Set<String> exports() {
		ensureMetaData();
		if (!meta.containsKey(EXPORT_FIELD)) {
			return Collections.emptySet();
		}
		else 
		{
			HashSet<String> ids = new HashSet<String>();
			JsonArray values = (JsonArray) meta.get(EXPORT_FIELD);
			for (JsonValue value : values) {
				ids.add(value.toString());
			}
			return Collections.unmodifiableSet(ids);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.jaql.lang.core.IModule#loadImports(com.ibm.jaql.lang.core.NamespaceEnv)
	 */
	public void loadImports(Namespace namespace) {
		ensureMetaData();
		if(!meta.containsKey(IMPORT_FIELD)) {
			return;
		} else {
			JsonArray imports = (JsonArray) meta.get(IMPORT_FIELD);
			for (JsonValue i : imports) {
				if (i instanceof JsonString) 
				{
					namespace.importNamespace(Namespace.get(i.toString()));
				}
				else if(i instanceof JsonRecord) {
					JsonRecord rec = (JsonRecord) i;
					String name = rec.get(new JsonString("module")).toString();
					JsonArray vars = (JsonArray) rec.get(new JsonString("vars"));
					ArrayList<String> ids = new ArrayList<String>();
					for (JsonValue id : vars) {
						ids.add(id.toString());
					}
					/*
					 * There are two cases here, either it is just a record with one entry "*",
					 * or a list of function names
					 */
					if(ids.size() == 1 && ids.get(0).equals("*")) {
						namespace.importAllFrom(Namespace.get(name));
					} else {
						namespace.importFrom(Namespace.get(name), ids);
					}
				}
			}
		}
	}
	
	public abstract File[] getJaqlFiles();
	public abstract File[] getJarFiles();
	public abstract File[] getExampleFiles();
	public abstract File getTestDirectory();
	public abstract String[] getTests();
	public abstract JsonRecord getModuleDescription();
	protected abstract InputStream getMetaDataStream();
}