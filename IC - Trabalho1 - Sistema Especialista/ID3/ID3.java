import java.io.IOException;

public class ID3 {

	public static void main(String[] args) {

		DataSet dataset = null;
		
		ParseFile databaseFile = new ParseFile();
		databaseFile.setArquivo("./dados/objetos.txt");
		try {
			dataset = databaseFile.getRegistros();
		} catch (IOException e) {
			System.err.println("Ops! Problemas ao ler o arquivo.");
			System.err.println("ERRO" + e.getMessage());
		}

		// Definindo o atributo que queremos usar como classe
		dataset.setAtributoDeClasse("Objeto");
		
		// Criando e construindo a �rvore de decis�o usando como base de treino
		// o conjunto de dados representado pelo objeto dataset.
		DecisionTree arvDecisao = new DecisionTree();
		arvDecisao.construir(dataset);
		
		// Exibindo a �rvore de Decis�o ap�s a sua constru��o
		System.out.println("--:: �rvore de Decis�o para esse conjunto de dados ::--");
		System.out.println(arvDecisao);
		
		// The life can't be more easy Padawan, all data load was automated!
		// May the force be with you! :)
		System.exit(0);
	}
}
