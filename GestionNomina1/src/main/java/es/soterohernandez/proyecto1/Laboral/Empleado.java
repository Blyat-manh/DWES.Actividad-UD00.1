package es.soterohernandez.proyecto1.Laboral;

public class Empleado extends Persona{
	private int categoria;
	int anyos;
	
	public Empleado(String nombre, String dni, char sexo, int categoria,int anyo)  throws DatosNoCorrectosException {
		super(nombre, dni, sexo);
		
		if(this.categoria>=1 && this.categoria<=10 && this.anyos>=1) {
			
			throw new DatosNoCorrectosException("Ha introducido mal los datos, revise porfavor.");
			
		}else {
			
			this.categoria=categoria;
			this.anyos=anyo;
		}
	}

	public Empleado(String nombre, String dni, char sexo) {
		super(nombre, dni, sexo);
		this.categoria=1;
		this.anyos=0;
	}

	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {

		if(this.categoria>=1 && this.categoria<=10) {
			this.categoria=categoria;
		}else {
			System.out.println("Ha introducido mal los datos, revise porfavor.");
		}
	}

	public void incrAnyo() {
		this.anyos++;
	}
	public void imprime() {
		System.out.println(" [nombre=" + nombre + ", dni=" + dni+ ",  sexo=" + sexo +" categoria=" + categoria + ", anyos=" + anyos + "]");
	}

	public int getAnyos() {
		return anyos;
	}
	
}
