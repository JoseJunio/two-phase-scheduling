package Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parameters {

	private int NUM_MACHINES;
	private int NUM_TASKS;
	private String fileName;
	private String filePaulo;

	// Algoritmo Genético
	private int numIteration;
	private int populacaoSize;
	private int tipoGeracao;

	public Parameters(String path) {

		String pathFile = System.getProperty("user.dir") + path;

		File file = new File(pathFile);
		FileReader fileReader;

		try {

			fileReader = new FileReader(file);

			Pattern p = Pattern.compile("(?:.*?= )(.*?)(?=\\n\\d:|$)");

			BufferedReader br = new BufferedReader(fileReader);

			String line;
			int count = 0;

			while ((line = br.readLine()) != null) {

				Matcher m = p.matcher(line);

				if (m.matches()) {
					switch (count) {
					case 0:
						NUM_MACHINES = Integer.parseInt(m.group(1));
						break;
					case 1:
						NUM_TASKS = Integer.parseInt(m.group(1));
						break;
					case 2:
						fileName = m.group(1);
						break;
					case 3:
						filePaulo = m.group(1);
						break;
					case 4:
						numIteration = Integer.parseInt(m.group(1));
						break;
					case 5:
						populacaoSize = Integer.parseInt(m.group(1));
						break;
					case 6:
						tipoGeracao = Integer.parseInt(m.group(1));
						break;
					}
					count++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int get_NUM_MACHINES() {
		return NUM_MACHINES;
	}

	public int get_NUM_TASKS() {
		return NUM_TASKS;
	}

	public String getFileName() {
		return fileName;
	}

	public int getNumIteration() {
		return numIteration;
	}

	public String getFilePaulo() {
		return filePaulo;
	}

	public int getPopulacaoSize() {
		return populacaoSize;
	}

	public int getTipoGeracao() {
		return tipoGeracao;
	}

}
