import java.util.Scanner;

public class Interface {

	public static void main(String[] args) 
	{
		boolean continuar;
		String resposta;
		Scanner sc = new Scanner(System.in);
		MotorDeInferencia motorDeInferencia = new MotorDeInferencia();
		
		do 
		{

			motorDeInferencia.iniciar();
			
			do {
				System.out.println("INTERFACE | Encontrou resposta = " + motorDeInferencia.encontrouResposta());
				System.out.println(motorDeInferencia.proximaPergunta() + "(S-Sim | N-Não)");
				//Le a resposta
				resposta = sc.next();
				motorDeInferencia.respondeParametroAtual(resposta.toUpperCase());
			}while (!motorDeInferencia.encontrouResposta());
			
			System.out.println(motorDeInferencia.getRespostaFinal());
			
			//Realiza a pergunta
			System.out.println("Continuar (S-Sim | N-Não)");
			//Le a resposta
			resposta = sc.next();
			
			if(resposta.toUpperCase().equals("S"))
			{
				continuar = true;
			}
			else
			{
				continuar = false;
			}
			
		} while (continuar);
		
		

	}
}
