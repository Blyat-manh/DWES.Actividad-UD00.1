package es.soterohernandez.proyecto1.Laboral;

public class CalculaNominas {
	static Nomina n= new Nomina(); ;
	public static void main(String[] args) {
		
		Empleado e1;
		try {
			 e1 = new Empleado("James", "32000032G", 'M' ,4,7);
		
		Empleado e2=new Empleado("Ada Lovelace", "32000031G",'F');
			
		escribe(e1);
		escribe(e2);
		
		e2.incrAnyo();
		e1.setCategoria(9);
		
		escribe(e1);
		escribe(e2);
		} catch (DatosNoCorrectosException e) {
			e.mensaje();
		}
	}
	private static void escribe (Empleado e) {
		
		System.out.println("Los atributos del empleado seleccionado son:");
		e.imprime();
		System.out.println("Y su sueldo es: "+n.sueldo(e));
		System.out.println("----------------------");
		
	}
}
