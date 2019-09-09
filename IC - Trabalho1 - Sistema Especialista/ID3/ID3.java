import java.io.IOException;

public class ID3 {
	public static void main(String[] args) {

		DataSet dataset = null;
		
		ParseFile databaseFile = new ParseFile();
		databaseFile.setArquivo("./Dados/animais.txt");
		try {
			dataset = databaseFile.getRegistros();
		} catch (IOException e) {
			System.err.println("Ops! Problemas ao ler o arquivo.");
			System.err.println("ERRO" + e.getMessage());
		}

		// Definindo o atributo que queremos usar como classe
		dataset.setAtributoDeClasse("Animal");
		
		// Criando e construindo a árvore de decisão usando como base de treino
		// o conjunto de dados representado pelo objeto dataset.
		DecisionTree arvDecisao = new DecisionTree();
		arvDecisao.construir(dataset);
		
		// Exibindo a Árvore de Decisão após a sua construção
		System.out.println("--:: Árvore de Decisão para esse conjunto de dados ::--");
		System.out.println(arvDecisao);
	
	}
}
