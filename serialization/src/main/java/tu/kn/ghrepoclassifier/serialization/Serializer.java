package tu.kn.ghrepoclassifier.serialization;

import org.kohsuke.github.GHRepository;
import tu.kn.ghrepoclassifier.serialization.data.RepoData;

import java.io.*;

/**
 * Created by felix on 04.12.16.
 */
public class Serializer {
	
	public static boolean exists(GHRepository repo, File dir) {
		return new File(dir.getAbsolutePath() + "/data" + repo.getId() + ".bin").exists();
	}

	public static void writeToDir(File dir, RepoData data) {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {
			fout = new FileOutputStream(dir.getAbsolutePath() + "/data" + data.getId() + ".bin");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(data);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static RepoData readFromFile(File file) {
		return readFromFile(file.getAbsolutePath());
	}

	public static RepoData readFromFile(String filename) {

		RepoData repo = null;

		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(filename);
			ois = new ObjectInputStream(fin);
			repo = (RepoData) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return repo;
	}
}
