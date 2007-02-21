package com.icesoft.metadata.generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultWriter {

	public static void main(String[] args) {

		PrintWriter outputWriter = new PrintWriter(System.out);
		outputWriter.write(44);

		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					"file.out"));
			PrintWriter out = new PrintWriter(bufferedWriter);

			bufferedWriter.write(32);
			bufferedWriter.write("test");
			bufferedWriter.flush();
			bufferedWriter.close();

			StringWriter sw = new StringWriter();
			sw.write(32);
			//sw.flush();
			
			PrintWriter two = new PrintWriter(System.out);
			two.write(10);
			two.write("\n");
			two.write("test");
			two.flush();
			two.close();
			//System.out.println( );

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setOutputWriter(new BufferedWriter(new FileWriter(outputFile)));
	}

}
