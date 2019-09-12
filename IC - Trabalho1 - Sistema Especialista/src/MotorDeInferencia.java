import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;



public class MotorDeInferencia {
	
	private boolean atende_regra, encontrou_resposta = false;
	private String resposta_final;
	private String parametro;
	
	
	public MotorDeInferencia() {
	}

	public boolean getAtendeRegra() {
		return atende_regra;
	}
	
	private void setParametro(String novo_parametro){
		parametro = novo_parametro;
	}
	
	public String getParametro(){
		return parametro;
	}
	
	public boolean encontrouResposta() {
		return encontrou_resposta;
	}
	
	public String getRespostaFinal() {
		return resposta_final;
	}
	
	//Metodo utilizado pela Interface para obter a próxima pergunta
	public String proximaPergunta() { 
		return inferirRegra();
	}
	private String inferirRegra() {
		String regra, token;
		int indice_token, tamanho_token = 0;
		
		try {
			//Abre o arquivo de que contém as regras do sistema 
			FileInputStream 	arquivo = new FileInputStream("dados/banco_de_conhecimento.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);

			//Le uma regra contida na linha do arquivo de texto
			regra = buffer.readLine();
			
			//Enquanto não acabarem as regras ou não encontrar uma resposta
			while(regra != null && !encontrou_resposta)
			{
				//Assume que a resposta é possível
				atende_regra = true;
				
				String resposta_esperada, resposta_usuario;
				
				//Busca um token entre espaços.
				//Incrementando o indice de leitura próxima posição: tamanho do token lido + 1
				for(indice_token = 3; indice_token < regra.indexOf("entao"); indice_token += (tamanho_token+1)) 
				{
										
					//System.out.println("|||| MOTOR ||||| REGRA AVALIADA = " + regra);
					
					//Obtém da regra lida, uma substring até o próximo espaço vazio. Encontrando, por exemplo> INANIMADO=não
					token = regra.substring(indice_token, regra.indexOf(" ", indice_token));
					
					//System.out.println("|||| MOTOR ||||| TOKEN DA REGRA = " + token);
					
					//Verifica o tamanho do token, que será o passo até a proxima leitura.
					tamanho_token = token.length();
					
					//Se o token lido contiver "=" é uma parâmetro a ser tratado
					if (token.contains("="))
					{
						//Divive o token pelo "=". Gerando> condicao[0]=INANIMADO e condicao[1]=não
						String condicao[] = token.split("=");
						
						//System.out.println("|||| MOTOR >>>>>>>>>>>>>>>> AVALIANDO PARÂMETRO = " + condicao[0]);
						
						setParametro(condicao[0]);
						resposta_esperada = condicao[1];
						
						//Recupera resposta da memória de trabalho
						resposta_usuario = recuperaParametroDaMemoria(parametro);
						
						//Caso este parâmetro já tenha sido respondido pelo usuário
						if(resposta_usuario != null)
						{
							//Verifica a resposta referente ao parâmetro (a direita do parâmetro)
							condicao = resposta_usuario.split("=");
							//Esta será a resposta do usuário em memória de trabalho
							resposta_usuario = condicao[1];
							
							//Verifica se o parâmetro respondido pelo usuário é igual ao esperado.
							atende_regra = parametroAtendeRegra(resposta_usuario, resposta_esperada);
							
							//System.out.println("|||| MOTOR  -  PARÂMETRO = " + condicao[0] + " ======= ATENDE REGRA = " + atende_regra);
						}
						
						//Caso o parâmetro ainda não tenha sido respondido é necessário interromper
						//Realizar a pergunta, enviando para interface uma próxima pergunta.
						else
						{
							//System.out.println("|||| MOTOR ||||| SEM RESPOSTA PARA PARÂMETRO = " + condicao[0]);
							
							//Finaliza o buffer e o acesso ao arquivo
							buffer.close();
							arquivo.close();

							//Retorna a pergunda referente a este parâmetro
							return buscarPerguntaPara(parametro);
						}
						
						//Caso cheque ao final com a regra ainda atendida
						if (!atende_regra) 
						{
							//Avança para próxima regra
							regra = buffer.readLine();
							break;
							
						} else {
							if((indice_token+tamanho_token+1) == regra.indexOf("entao"))
							{
								//Esta é a resposta, armazena para ser obtida pela interface
								resposta_final = ("Você pensou em " + regra.substring(regra.indexOf("entao")+6));
								//Avisa ao sistema por flag que já encontrou resposta
								encontrou_resposta = true;	
							}
						}
						
						//System.out.println("Encontrou parametro = " + parametro);
						//System.out.println("Resposta Esperada   = " + resposta_esperada);
						//System.out.println("Resposta do Usuário = " + resposta_usuario);
						//System.out.println("Regra atendida      = " + atende_regra);
					}
					
				}
				//System.out.println("Chegou ao final com regra atendida = " + atende_regra);
				
				
			}
			
			
			//Finaliza o buffer e o acesso ao arquivo
			buffer.close();
			arquivo.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Erro ao abrir o arquivo: " + e.toString());
		}
		return parametro;
	}
	
	
	private static boolean parametroAtendeRegra(String resposta_usuario, String resposta_esperada) {
		if(resposta_usuario.equals(resposta_esperada))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static String recuperaParametroDaMemoria(String parametro) {
		String resposta = null;
		
		try 
		{
			//Abre o arquivo de que contém as repostas dadas pelo usuário 
			FileInputStream 	arquivo = new FileInputStream("dados/memoria_de_trabalho.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);
			
			//Armazena a linha atual, que contém uma resposta
			resposta  = buffer.readLine();
			
			while(resposta != null)
			{
				//Haverá somente uma reposta para cada parâmetro
				//SE o texto do parâmetro existir na reposta, esta é a resposta
				if(resposta.contains(parametro))
				{
					//Finaliza o buffer e o acesso ao arquivo
					buffer.close();
					arquivo.close();
					
					//Retorna a String contendo a reposta
					return resposta;
				}
				//Le a próxima regra, até encontrar a pergunta do parâmetro lido
				resposta  = buffer.readLine();
			}
			//Finaliza o buffer e o acesso ao arquivo
			buffer.close();
			arquivo.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Erro ao abrir o arquivo: " + e.toString());
		}
		
		return resposta;
	}
	
	//Conforme o parâmetro emcontrado na leitura da regra, busca uma resposta
	public void respondeParametroAtual(String resposta) {
		
		boolean resposta_usuario;
		
		if(resposta.equals("S"))
		{
			resposta_usuario = true;
		}
		else
		{
			resposta_usuario = false;
		}
		
		adicionarMemoria(parametro + "=" + resposta_usuario);
		
		inferirRegra();
		
	}
	
	private static String buscarPerguntaPara(String parametro){

		String regra = null;
		
		try 
		{
			//Abre o arquivo de que contém as regras do sistema 
			FileInputStream 	arquivo = new FileInputStream("dados/banco_de_conhecimento.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);
			
			//Armazena uma regra lida
			regra  = buffer.readLine();
			
			while(regra != null)
			{
				//Busca somente por perguntas, regras no formato:
				//SE PARAMETRO faca PERGUNTA?
				//Haverá somente uma pergunta para cada parâmetro
				if(regra.contains(parametro) && regra.contains("faca"))
				{
					//Finaliza o buffer e o acesso ao arquivo
					buffer.close();
					arquivo.close();
					
					//Retorna a String contendo somente a pergunta
					return regra.substring(regra.indexOf("faca ")+5);
				}
				//Le a próxima regra, até encontrar a pergunta do parâmetro lido
				regra  = buffer.readLine();
			}
			//Finaliza o buffer e o acesso ao arquivo
			buffer.close();
			arquivo.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Erro ao abrir o arquivo: " + e.toString());
		}
		//Retorna a pergunta a ser tratada para resolver o parâmetro
		//Retorna NULL caso não exista pergunta para o parâmetro passado
		return regra;
	}
	
	//Adiciona uma nova linha de registro ao arquivo memória de trabalho
	private static void adicionarMemoria(String memoria) {
		try {
			//Objeto writer para criar dados no arquivo sem necessidade de leitura
			PrintWriter writer = new PrintWriter( new FileWriter("Dados/memoria_de_trabalho.txt", true));
			
			//Escreve nova linha contenco o conteudo passado. Ex: MENOS4PATAS=false
			writer.println(memoria);

			//Encerra o processo de escrita, liberando o acesso.
			writer.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Erro ao salvar memória de trabalho: " + e.toString());
		}
	}
	
	//Metodo que iniciar ou reinicia o motor de inferência
	public void iniciar() {
		//Apaga o conteúdo da mamória de trabalho
		reiniciarMemoriaDeTrabalho();
		//Apaga os dados de resposta já encontrada
		encontrou_resposta = false;
		resposta_final = "";
	}
	
	private static void reiniciarMemoriaDeTrabalho() {
		try {
			FileOutputStream arquivo = new FileOutputStream("Dados/memoria_de_trabalho.txt"); 
			PrintWriter writer = new PrintWriter(arquivo);
			
			writer.print("");
			
			writer.close();
			arquivo.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Erro ao salvar memória de trabalho: " + e.toString());
		}
	}
}