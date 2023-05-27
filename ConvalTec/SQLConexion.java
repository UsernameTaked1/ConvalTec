import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

public class SQLConexion{

    private Connection conexion;
    private java.sql.Statement sentencia;
    private DatabaseMetaData datosDB;
    
    public SQLConexion(){

    }
    
    public void conectar() {//Realiza la conexión a la base de datos
        try {
            conexion = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;"
                    + "database=Convalidaciones;"
                    + "integratedSecurity=true;"
                    + "trustServerCertificate=true");
            datosDB = conexion.getMetaData();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void consultaJCB (String statement, JComboBox j) throws SQLException{//realiza una consulta y guarda el resultado en un JComboBox que proporcionemos
        String consulta = statement;
            sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(consulta);
            j.removeAllItems();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            String r="";
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                j.addItem(rs.getString(i).trim());
            }
        }
    }
   
    public String printConsulta (String statement) throws SQLException{//Devuelve el resultado de una consulta en una variable String
        String r = "";
        String consulta = statement;
        sentencia = conexion.createStatement();
        ResultSet rs = sentencia.executeQuery(consulta);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int columnCount = rsMetaData.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                r = r + rs.getString(i) + "\n";
            }
        }
        return r;
    }

    public void execute(String statement) throws SQLException {//Realiza la ejecución de una sentencia que indiquemos con un String
        String consulta = statement;
        sentencia = conexion.createStatement();
        int filasAfectadas = sentencia.executeUpdate(consulta);
        System.out.println("Filas afectadas: " + filasAfectadas);
    }

    public DefaultTableModel generarTabla(String statement) throws SQLException {//genera un modelo de tabla de acuerdo a alguna consulta que indiquemos para poder aplicarlo a algún JTable
        DefaultTableModel model = new DefaultTableModel();
        String datostabla = statement;
        sentencia = conexion.createStatement();
        ResultSet rs = sentencia.executeQuery(datostabla);
    
        // Obtener los metadatos de la consulta
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
    
        // Agregar las columnas al modelo de datos
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            model.addColumn(metaData.getColumnLabel(columnIndex));
        }
    
        // Agregar las filas al modelo de datos
        while (rs.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = rs.getObject(i);
            }
            model.addRow(rowData);
        }
        return model;
    }
    
}