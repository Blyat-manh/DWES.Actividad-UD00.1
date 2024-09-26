package es.soterohernandez.proyecto1.Laboral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class CalculaNominas {
	static Nomina n = new Nomina();
	static String rutaArchivoTxt = "C:\\Users\\usuario\\eclipse-workspace\\GestionNomina2\\empleados.txt";
	static String rutaArchivoTxt2 = "C:\\Users\\usuario\\eclipse-workspace\\GestionNomina2\\empleadosNuevos.txt";
	private static final String url = "jdbc:mysql://localhost:3306/gestionNominas2";
	private static final String user = "root";
	private static final String pass = "";

	public static void main(String[] args) throws DatosNoCorrectosException, IOException {
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			System.out.println("Conexión exitosa!");

			Set<String> existingDnis = getDniExistente(connection);
			comprobarEmpleados(connection);

			altaEmpleadoDesdeArchivo(rutaArchivoTxt, existingDnis);
			altaEmpleadoDesdeArchivo(rutaArchivoTxt2, existingDnis);

			Scanner sc = new Scanner(System.in);
			boolean salir = false;
			int opcion;

			while (!salir) {
				System.out.println("----------------------------------------------------------");
				System.out.println("1. Mostrar los datos de todos los empleados.");
				System.out.println("2. Mostar salario de empleado.");
				System.out.println("3. Edicion de empleados.");
				System.out.println("4. Recalcular y actualizar el sueldo de un empleado.");
				System.out.println("5. Recalcular y actualizar los sueldos de tods los empleados.");
				System.out.println("6. Copia de seguridad.");
				System.out.println("7. Salir");

				try {

					System.out.println("Escribe una de las opciones");
					opcion = sc.nextInt();

					switch (opcion) {
					case 1:
						// mostrar todos los empleados de la base de datos.
						String query1 = "SELECT nombre, dni, sexo, categoria, anyos FROM empleado";
						try (PreparedStatement preparedStatement = connection.prepareStatement(query1);
								ResultSet resultSet = preparedStatement.executeQuery()) {
							while (resultSet.next()) {
								String nombre = resultSet.getString("nombre");
								String dni = resultSet.getString("dni");
								char sexo = resultSet.getString("sexo").charAt(0);
								int categoria = resultSet.getInt("categoria");
								int anyos = resultSet.getInt("anyos");

								Empleado e = new Empleado(nombre, dni, sexo, categoria, anyos);
								escribe(e);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					case 2:
						// Mostar salario de empleado.
						System.out.println("Indique el dni del empleado a buscar: \n");
						String dniBus = sc.next();
						String query2 = "SELECT dni, salario FROM Salarios WHERE dni LIKE ?";
						try (PreparedStatement preparedStatement = connection.prepareStatement(query2)) {
							preparedStatement.setString(1, dniBus);
							ResultSet resultSet = preparedStatement.executeQuery();

							if (resultSet.next()) {
								String dni2 = resultSet.getString("dni");
								String salario = resultSet.getString("salario");

								System.out.println(
										"El empleado con dni: " + dni2 + ", tiene un salario de " + salario + "\n");
							} else {
								System.out.println("No se encontró el empleado con el DNI especificado.");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					case 3:
						System.out.println("Indique el DNI del empleado a editar:");
						String dniEdit = sc.next();
						Empleado empleadoEditado = obtenerEmpleadoPorDni(connection, dniEdit);

						if (empleadoEditado != null) {
							boolean salirSubmenu = false;

							while (!salirSubmenu) {
								System.out.println("Seleccione el campo que desea editar:");
								System.out.println("1. Nombre");
								System.out.println("2. Sexo");
								System.out.println("3. Categoría");
								System.out.println("4. Años trabajados");
								System.out.println("5. Salir del menú de edición");

								int opcionSubmenu = sc.nextInt();

								switch (opcionSubmenu) {
								case 1:
									System.out.println("Introduzca el nuevo nombre:");
									String nuevoNombre = sc.next();
									empleadoEditado.setNombre(nuevoNombre);
									break;
								case 2:
									System.out.println("Introduzca el nuevo sexo (M/F):");
									char nuevoSexo = sc.next().charAt(0);
									empleadoEditado.setSexo(nuevoSexo);
									break;
								case 3:
									System.out.println("Introduzca la nueva categoría:");
									int nuevaCategoria = sc.nextInt();
									empleadoEditado.setCategoria(nuevaCategoria);
									break;
								case 4:
									System.out.println("Introduzca los nuevos años de servicio:");
									int nuevosAnyos = sc.nextInt();
									empleadoEditado.setAnyos(nuevosAnyos);
									break;
								case 5:
									salirSubmenu = true;
									break;
								default:
									System.out.println("Opción no válida. Por favor, seleccione de nuevo.");
									break;
								}
							}
							// Actualizar la base de datos q si no no va amh
							actualizarEmpleado(connection, empleadoEditado);
						} else {
							System.out.println("Empleado no encontrado con el DNI especificado.");
						}

						break;

					case 4:
						// Recalcular y actualizar el sueldo de un empleado.
						System.out.println("Indique el dni del empleado: ");
						String dni4 =sc.next();
						obtenerEmpleadoPorDni(connection, dni4);
						guardaSalario(connection,obtenerEmpleadoPorDni(connection, dni4));
						break;
					case 5:
						// Recalcular y actualizar los sueldos de tods los empleados
						String query5 = "SELECT nombre, dni, sexo, categoria, anyos FROM empleado";
						try (PreparedStatement preparedStatement = connection.prepareStatement(query5);
								ResultSet resultSet = preparedStatement.executeQuery()) {
							while (resultSet.next()) {
								String nombre = resultSet.getString("nombre");
								String dni5 = resultSet.getString("dni");
								char sexo = resultSet.getString("sexo").charAt(0);
								int categoria = resultSet.getInt("categoria");
								int anyos = resultSet.getInt("anyos");

								Empleado e = new Empleado(nombre, dni5, sexo, categoria, anyos);

								guardaSalario(connection, e);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					case 6:
						// Copia de seguridad
						String query = "SELECT nombre, dni, sexo, categoria, anyos FROM empleado";
						try (PreparedStatement preparedStatement = connection.prepareStatement(query);
								ResultSet resultSet = preparedStatement.executeQuery()) {
							while (resultSet.next()) {

								String nombre = resultSet.getString("nombre");
								String dni = resultSet.getString("dni");
								char sexo = resultSet.getString("sexo").charAt(0);
								int categoria = resultSet.getInt("categoria");
								int anyos = resultSet.getInt("anyos");

								String datos = nombre + ", " + dni + ", " + sexo + ", " + categoria + ", " + anyos;

								copiaSeguridad(datos);

							}
						}

						break;
					case 7:
						salir = true;
						break;
					default:
						System.out.println("Solo números entre 1 y 7");
					}
				} catch (InputMismatchException e) {
					System.out.println("Debes insertar un número");
					sc.next();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

//--------------------------------------------------------------------------------------------------------
	private static Empleado obtenerEmpleadoPorDni(Connection connection, String dni)
			throws SQLException, DatosNoCorrectosException {
		String query = "SELECT nombre, dni, sexo, categoria, anyos FROM empleado WHERE dni = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, dni);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String nombre = resultSet.getString("nombre");
				char sexo = resultSet.getString("sexo").charAt(0);
				int categoria = resultSet.getInt("categoria");
				int anyos = resultSet.getInt("anyos");
				return new Empleado(nombre, dni, sexo, categoria, anyos);
			}
		}
		return null; // Return null if not found
	}

	private static void actualizarEmpleado(Connection connection, Empleado empleado) throws SQLException {
		String updateQuery = "UPDATE empleado SET nombre = ?, sexo = ?, categoria = ?, anyos = ? WHERE dni = ?";
		try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
			updateStatement.setString(1, empleado.getNombre());
			updateStatement.setString(2, String.valueOf(empleado.getSexo()));
			updateStatement.setInt(3, empleado.getCategoria());
			updateStatement.setInt(4, empleado.getAnyos());
			updateStatement.setString(5, empleado.getDni());
			updateStatement.executeUpdate();
			guardaSalario(connection, empleado);
			System.out.println("Empleado actualizado correctamente.");
		}
	}

	private static void copiaSeguridad(String datos) throws IOException {
		FileWriter writer = new FileWriter("C:\\Users\\usuario\\eclipse-workspace\\GestionNomina2\\CopiaSeguridad.txt",
				true);

		writer.write(datos + "\n");
		writer.close();
	}

	private static Set<String> getDniExistente(Connection connection) throws SQLException {
		Set<String> existingDnis = new HashSet<>();
		String query = "SELECT dni FROM empleado";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				existingDnis.add(resultSet.getString("dni"));
			}
		}
		return existingDnis;
	}

	private static void comprobarEmpleados(Connection connection) throws SQLException, DatosNoCorrectosException {
		String query = "SELECT nombre, dni, sexo, categoria, anyos FROM empleado";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				String nombre = resultSet.getString("nombre");
				String dni = resultSet.getString("dni");
				char sexo = resultSet.getString("sexo").charAt(0);
				int categoria = resultSet.getInt("categoria");
				int anyos = resultSet.getInt("anyos");

				Empleado e = new Empleado(nombre, dni, sexo, categoria, anyos);
//				escribe(e);
				guardaSalario(connection, e);
			}
		}
	}

	private static void escribe(Empleado e) {
		e.imprime();
		System.out.println("Y su sueldo es: " + n.sueldo(e));
		System.out.println("-------------------------------------");
	}

	private static void guardaSalario(Connection connection, Empleado e) throws SQLException {
		String query = "SELECT COUNT(*) FROM Salarios WHERE dni = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, e.getDni());
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			int count = resultSet.getInt(1);

			if (count == 0) {
				String query2 = "INSERT INTO Salarios (dni, salario) VALUES (?, ?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(query2)) {
					insertStatement.setString(1, e.getDni());
					insertStatement.setDouble(2, n.sueldo(e));
					insertStatement.executeUpdate();
					System.out.println(
							"Salario guardado en la base de datos: DNI = " + e.getDni() + ", Salario = " + n.sueldo(e));
				}
			}
//            else {
//                System.out.println("El salario para el DNI " + e.getDni() + " ya existe.");
//            } mensaje de verificacion, ignorar por ahora
		}
	}

	public static void altaEmpleadoDesdeArchivo(String filePath, Set<String> existingDnis) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length != 5) {
					System.out.println("Formato de línea incorrecto: " + line);
					continue;
				}
				String nombre = data[0].trim();
				String dni = data[1].trim();
				char sexo = data[2].trim().charAt(0);
				int categoria = Integer.parseInt(data[3].trim());
				int anyos = Integer.parseInt(data[4].trim());

				if (!existingDnis.contains(dni)) {
					try {
						Empleado e = new Empleado(nombre, dni, sexo, categoria, anyos);
						altaEmpleado(e);
					} catch (DatosNoCorrectosException ex) {
						System.out.println("Error al crear empleado: " + ex.getMessage());
					}
				}
//                else {
//                    System.out.println("El empleado con DNI " + dni + " ya existe en la base de datos.");
//                } mensaje de verificacion, ignorar por ahora
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void altaEmpleado(Empleado e) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, user, pass)) {
			String insertQuery = "INSERT INTO empleado (nombre, dni, sexo, categoria, anyos) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
				insertStatement.setString(1, e.getNombre());
				insertStatement.setString(2, e.getDni());
				insertStatement.setString(3, String.valueOf(e.getSexo()));
				insertStatement.setInt(4, e.getCategoria());
				insertStatement.setInt(5, e.getAnyos());
				insertStatement.executeUpdate();
				guardaSalario(connection, e);
			}
		}
	}
}
