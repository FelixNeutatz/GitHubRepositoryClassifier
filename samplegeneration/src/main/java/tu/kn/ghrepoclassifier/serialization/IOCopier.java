package tu.kn.ghrepoclassifier.serialization;

import java.io.*;

import org.apache.commons.io.IOUtils;

/**
 * Created by felix on 28.11.16.
 */
public class IOCopier {
	
	public static void joinFilesFromDir(File destination, File sourceDirectory) 
		throws IOException {
		File[] sources = sourceDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".csv");
			}
		});
		joinFiles(destination, sources);
	}
		
	
	public static void joinFiles(File destination, File[] sources)
		throws IOException {
		OutputStream output = null;
		try {
			output = createAppendableStream(destination);
			for (File source : sources) {
				appendFile(output, source);
			}
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	private static BufferedOutputStream createAppendableStream(File destination)
		throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(destination, true));
	}

	private static void appendFile(OutputStream output, File source)
		throws IOException {
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(source));
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
