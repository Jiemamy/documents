package com.docbook.extract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class to extract code from java source files into XML files that can be
 * included in DocBook books.
 * 
 * The class will search for annotated java source files and extract the annotated source
 * as an XML file.
 * 
 */
public class Extractor {

	protected String startAnnotation = "//@extract-start";
	
	protected String endAnnotation = "//@extract-end";

	protected File targetDir;
	
	protected String root;

	public Extractor(String root, File targetDir) {
		this.root = root;
		this.targetDir = targetDir;
	}

	public static void main(String[] args) throws IOException {
		File sourceDir = new File(args[0]);
		File targetDir = new File(args[1]);

		Extractor extractor = new Extractor(sourceDir.getAbsolutePath(), targetDir);
		extractor.extractCode(sourceDir);

	}

	/**
	 * 
	 * @param sourceDir
	 *            containing java source files
	 * @throws IOException 
	 */
	public void extractCode(File sourceDir) throws IOException {
		System.out.println("Extracting dir " + sourceDir);

		// List the source directory. If the file is a dir recurse,
		// if the file is a java file check for Extract annotations
		// otherwise ignore

		File[] elements = sourceDir.listFiles();

		for (int i = 0; i < elements.length; ++i) {
			File file = elements[i];
			if (file.isDirectory()) {
				extractCode(file);
			} else if (file.getName().endsWith(".java")
					&& file.getName().equals("Extractor.java") == false) {
				extractAnnotatedCode(file);
			} // fi
		} // rof
	}

	public void extractAnnotatedCode(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		boolean extract = false;
		int index = 0;
		BufferedWriter writer = null;

		while ((line = reader.readLine()) != null) {
			if (extract) {
				if (line.contains(endAnnotation)) {
					closeFile(writer);
					extract = false;
					writer = null;
					continue;
				} else {
					writer.append(line.substring(index));
					writer.newLine();
				}
			} else if (line.contains(startAnnotation)) {
				index = line.indexOf(startAnnotation);
				String name = line.substring(index + startAnnotation.length()).trim();
				extract = true;
				writer = createXiIncludeFile(name);
			}
		}

		if (writer != null) {
			closeFile(writer);
		}
	}

	public BufferedWriter createXiIncludeFile(String file) throws IOException {
		File targetFile = new File(targetDir, file + ".xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));

		// write
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.newLine();
		writer.write("<para>");
		writer.newLine();
		writer.write("<programlisting><![CDATA[");
		writer.newLine();
		return writer;
	}

	public void closeFile(BufferedWriter writer) throws IOException {
		writer.write("...]]>");
		writer.newLine();
		writer.write("</programlisting>");
		writer.newLine();
		writer.write("</para>");
		writer.flush();
		writer.close();
	}

}
