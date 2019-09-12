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
	
	//Metodo utilizado pela Interface para obter a pr�xima pergunta
	public String proximaPergunta() { 
		return inferirRegra();
	}
	private String inferirRegra() {
		String regra, token;
		int indice_token, tamanho_token = 0;
		
		try {
			//Abre o arquivo de que cont�m as regras do sistema 
			FileInputStream 	arquivo = new FileInputStream("dados/banco_de_conhecimento.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);

			//Le uma regra contida na linha do arquivo de texto
			regra = buffer.readLine();
			
			//Enquanto n�o acabarem as regras ou n�o encontrar uma resposta
			while(regra != null && !encontrou_resposta)
			{
				//Assume que a resposta � poss�vel
				atende_regra = true;
				
				String resposta_esperada, resposta_usuario;
				
				//Busca um token entre espa�os.
				//Incrementando o indice de leitura pr�xima posi��o: tamanho do token lido + 1
				for(indice_token = 3; indice_token < regra.indexOf("entao"); indice_token += (tamanho_token+1)) 
				{
										
					//System.out.println("|||| MOTOR ||||| REGRA AVALIADA = " + regra);
					
					//Obt�m da regra lida, uma substring at� o pr�ximo espa�o vazio. Encontrando, por exemplo> INANIMADO=n�o
					token = regra.substring(indice_token, regra.indexOf(" ", indice_token));
					
					//System.out.println("|||| MOTOR ||||| TOKEN DA REGRA = " + token);
					
					//Verifica o tamanho do token, que ser� o passo at� a proxima leitura.
					tamanho_token = token.length();
					
					//Se o token lido contiver "=" � uma par�metro a ser tratado
					if (token.contains("="))
					{
						//Divive o token pelo "=". Gerando> condicao[0]=INANIMADO e condicao[1]=n�o
						String condicao[] = token.split("=");
						
						//System.out.println("|||| MOTOR >>>>>>>>>>>>>>>> AVALIANDO PAR�METRO = " + condicao[0]);
						
						setParametro(condicao[0]);
						resposta_esperada = condicao[1];
						
						//Recupera resposta da mem�ria de trabalho
						resposta_usuario = recuperaParametroDaMemoria(parametro);
						
						//Caso este par�metro j� tenha sido respondido pelo usu�rio
						if(resposta_usuario != null)
						{
							//Verifica a resposta referente ao par�metro (a direita do par�metro)
							condicao = resposta_usuario.split("=");
							//Esta ser� a resposta do usu�rio em mem�ria de trabalho
							resposta_usuario = condicao[1];
							
							//Verifica se o par�metro respondido pelo usu�rio � igual ao esperado.
							atende_regra = parametroAtendeRegra(resposta_usuario, resposta_esperada);
							
							//System.out.println("|||| MOTOR  -  PAR�METRO = " + condicao[0] + " ======= ATENDE REGRA = " + atende_regra);
						}
						
						//Caso o par�metro ainda n�o tenha sido respondido � necess�rio interromper
						//Realizar a pergunta, enviando para interface uma pr�xima pergunta.
						else
						{
							//System.out.println("|||| MOTOR ||||| SEM RESPOSTA PARA PAR�METRO = " + condicao[0]);
							
							//Finaliza o buffer e o acesso ao arquivo
							buffer.close();
							arquivo.close();

							//Retorna a pergunda referente a este par�metro
							return buscarPerguntaPara(parametro);
						}
						
						//Caso cheque ao final com a regra ainda atendida
						if (!atende_regra) 
						{
							//Avan�a para pr�xima regra
							regra = buffer.readLine();
							break;
							
						} else {
							if((indice_token+tamanho_token+1) == regra.indexOf("entao"))
							{
								//Esta � a resposta, armazena para ser obtida pela interface
								resposta_final = ("Voc� pensou em " + regra.substring(regra.indexOf("entao")+6));
								//Avisa ao sistema por flag que j� encontrou resposta
								encontrou_resposta = true;	
							}
						}
						
						//System.out.println("Encontrou parametro = " + parametro);
						//System.out.println("Resposta Esperada   = " + resposta_esperada);
						//System.out.println("Resposta do Usu�rio = " + resposta_usuario);
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
			//Abre o arquivo de que cont�m as repostas dadas pelo usu�rio 
			FileInputStream 	arquivo = new FileInputStream("dados/memoria_de_trabalho.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);
			
			//Armazena a linha atual, que cont�m uma resposta
			resposta  = buffer.readLine();
			
			while(resposta != null)
			{
				//Haver� somente uma reposta para cada par�metro
				//SE o texto do par�metro existir na reposta, esta � a resposta
				if(resposta.contains(parametro))
				{
					//Finaliza o buffer e o acesso ao arquivo
					buffer.close();
					arquivo.close();
					
					//Retorna a String contendo a reposta
					return resposta;
				}
				//Le a pr�xima regra, at� encontrar a pergunta do par�metro lido
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
	
	//Conforme o par�metro emcontrado na leitura da regra, busca uma resposta
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
			//Abre o arquivo de que cont�m as regras do sistema 
			FileInputStream 	arquivo = new FileInputStream("dados/banco_de_conhecimento.txt");
			InputStreamReader	input 	= new InputStreamReader(arquivo);
			BufferedReader		buffer 	= new BufferedReader(input);
			
			//Armazena uma regra lida
			regra  = buffer.readLine();
			
			while(regra != null)
			{
				//Busca somente por perguntas, regras no formato:
				//SE PARAMETRO faca PERGUNTA?
				//Haver� somente uma pergunta para cada par�metro
				if(regra.contains(parametro) && regra.contains("faca"))
				{
					//Finaliza o buffer e o acesso ao arquivo
					buffer.close();
					arquivo.close();
					
					//Retorna a String contendo somente a pergunta
					return regra.substring(regra.indexOf("faca ")+5);
				}
				//Le a pr�xima regra, at� encontrar a pergunta do par�metro lido
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
		//Retorna a pergunta a ser tratada para resolver o par�metro
		//Retorna NULL caso n�o exista pergunta para o par�metro passado
		return regra;
	}
	
	//Adiciona uma nova linha de registro ao arquivo mem�ria de trabalho
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
			System.out.println("Erro ao salvar mem�ria de trabalho: " + e.toString());
		}
	}
	
	//Metodo que iniciar ou reinicia o motor de infer�ncia
	public void iniciar() {
		//Apaga o conte�do da mam�ria de trabalho
		reiniciarMemoriaDeTrabalho();
		//Apaga os dados de resposta j� encontrada
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
			System.out.println("Erro ao salvar mem�ria de trabalho: " + e.toString());
		}
	}
}