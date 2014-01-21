package de.jugsaar.meeting6.java.troubleshooting;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class AppWithMemoryProblem {

	private static final String _10MB_DAT = "10mb.dat";

	static {
		try {
			Path filePath = Paths.get(_10MB_DAT);
			if (!Files.exists(filePath)) {
				try (OutputStream os = Files.newOutputStream(filePath,
						StandardOpenOption.CREATE_NEW)) {
					byte[] kilobyte = new byte[1024];
					for (int i = 0; i < 10 * 1024; i++) {
						os.write(kilobyte);
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	public static void main(String[] args) throws Exception {
		byte[] data = null;

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (i % 5000 == 0) {
				data = readFile(_10MB_DAT);
			}

			new DemoAction().upload(String.valueOf(i), data);
		}
	}

	private static byte[] readFile(String path) throws IOException {
		try (InputStream stream = new FileInputStream(path);
				BufferedInputStream in = new BufferedInputStream(stream);
				ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[in.available()];
			int size;
			while ((size = in.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			return out.toByteArray();
		}
	}

	static class DemoAction {
		protected static Map<String, DemoEntity> cache = new HashMap<>();
		protected DemoDao dao = new DemoDao();

		public void upload(String id, byte[] data) {
			DemoEntity entity = find(id);
			if (entity == null)
				dao.insert(id, data);
			dao.update(id, data);
		}

		protected DemoEntity find(String id) {
			String query = "select * from DEMO_TBL where id = " + id;
			if (cache.containsKey(query))
				return cache.get(query);
			DemoEntity result = dao.select(query);
			cache.put(query, result);
			return result;
		}
	}

	static class DemoEntity {
		byte[] data;
		String id;

		DemoEntity(String id, byte[] data) {
			this.id = id;
			this.data = data;
		}
	}

	static class DemoDao {

		private DemoEntity current;

		public void insert(String id, byte[] data) {
			this.current = new DemoEntity(id, data);
		}

		public DemoEntity select(String query) {

			String id = query.split("=")[1].trim();
			if (id.length() % 48 == 1) {
				return (current = new DemoEntity(id, null));
			} else {
				return null;
			}

		}

		public void update(String id, byte[] data) {
			current.id = id;
			current.data = data;
		}
	}
}
