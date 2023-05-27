import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


public class Convalidacion extends JFrame {
    private SQLConexion sql;
    private JTable materiasConvalidadas, infoAlumno;
    private JButton amateria, generar, volver, finalizar, eliminar;
    private JPanel table, mate, i, d, botones;
    private JComboBox<String> materiasAlumno, carreraConvalidar, materiasConvalidar;
    private String no_control, carrera;
    private TableModel tableModelConvalidacion, tableModelInfo;


    public Convalidacion(String num_control) throws SQLException {
        sql = new SQLConexion();
        no_control = num_control;
        i = new JPanel();
        d = new JPanel();
        mate = new JPanel();
        table = new JPanel();
        botones = new JPanel();
        infoAlumno = new JTable();
        materiasConvalidadas = new JTable();
        amateria = new JButton("Agregar");
        generar = new JButton("Generar");
        volver = new JButton("Regresar");
        finalizar = new JButton("Volver al menú");
        eliminar = new JButton("Eliminar Convalidación");
        materiasAlumno = new JComboBox<>();
        carreraConvalidar = new JComboBox<>();
        materiasConvalidar = new JComboBox<>();
        
        atributos();
        armado();
        escuchas();
        sql.conectar();
        carrera = sql.printConsulta("(SELECT carrera FROM alumno WHERE no_control = '" + no_control + "')");
        carrera = carrera.trim();
        inicializar();
    }

    private void atributos() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("ConvalTEC");
        setSize(1100, 600);
        setResizable(false);

        mate.setPreferredSize(new Dimension(0, 100));

        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        table.setPreferredSize(new Dimension(579, 483));

        i.setPreferredSize(new Dimension(50, 0));
        d.setPreferredSize(new Dimension(50, 0));

        botones.setBackground(new Color(96, 97, 99));
        mate.setBackground(new Color (240,240,240));
        table.setBackground(new Color(215,208,191));

        materiasConvalidadas.setEnabled(false);
        infoAlumno.setEnabled(false);
    }

    private void armado() {
        add(mate, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(i, BorderLayout.WEST);
        add(d, BorderLayout.EAST);
        add(botones,BorderLayout.SOUTH);

        JLabel carreraConvalidarLbl =  new JLabel("Carrera que desea cambiar:");
        mate.add(carreraConvalidarLbl);
        mate.add(carreraConvalidar);
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(0, 0, 0));
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(new Dimension(getWidth(), 1));
        mate.add(emptyPanel);
        JLabel materiasCursadas =  new JLabel("Materias Cursadas por el alumno:");
        mate.add(materiasCursadas);
        mate.add(materiasAlumno);
        JLabel materiasAConvalidar =  new JLabel("Materias a convalidar:");
        mate.add(materiasAConvalidar);
        mate.add(materiasConvalidar);
        mate.add(amateria);

        table.add(new JScrollPane(infoAlumno));
        table.add(new JScrollPane(materiasConvalidadas));

        botones.add(generar);
        botones.add(volver);
        botones.add(eliminar);
        botones.add(finalizar);
    }

    private void escuchas(){
        
        generar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int materiasConv [] = {200, 60, 200, 75, 50};
                    int infoA [] = {75, 100, 75, 75, 60, 200};
                    generarPDF(infoAlumno, infoA, materiasConvalidadas, materiasConv);
                    JOptionPane.showMessageDialog(null, "Archivo generado con éxito");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        volver.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Materias m;
                try {
                    m = new Materias(no_control);
                    m.mostrar(true);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        });
        finalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MenuConvalidacion menu;
                try {
                    menu = new MenuConvalidacion();
                    menu.mostrar(true);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
            }
        });
                
        carreraConvalidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sql.consultaJCB("select nombre from materias where carrera = '"+(String) carreraConvalidar.getSelectedItem()+"' order by nombre asc", materiasConvalidar);
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
            }      
        });

        amateria.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String numMA = sql.printConsulta("select no_materia from materias where nombre = '"+materiasAlumno.getSelectedItem()+"' and carrera = '"+carrera+"'");
                    numMA = numMA.trim();
                    String numMC = sql.printConsulta("select no_materia from materias where nombre = '"+materiasConvalidar.getSelectedItem()+"' and carrera = '"+carreraConvalidar.getSelectedItem()+"'");
                    numMC = numMC.trim();
                    sql.execute("insert into Convalidaciones values('"+numMA+"','"+numMC+"','"+no_control+"')");
                    tableModelConvalidacion = sql.generarTabla("Select DISTINCT a.nombre AS 'Asignatura Cursada', cu.calificacion AS 'Calificación', b.nombre AS 'Asignatura Convalidada', b.clave_materia AS 'Clave', b.creditos AS 'Créditos' from materias a, materias b, cursa cu, Convalidaciones c where a.no_materia = c.clave_materia_cursada and b.no_materia = c.clave_materia_convalidar and c.no_control = '"+no_control+"'and cu.clave_materia = c.clave_materia_cursada");
                    materiasConvalidadas.setModel(tableModelConvalidacion);
                } catch (SQLException e1) {
                    if(e1.getErrorCode() == 2627){
                        JOptionPane.showMessageDialog(null, "Error al agregar convalidación\nError: Esta convalidación ya ha sido agregada");
                    }else{
                    JOptionPane.showMessageDialog(null, "Error al agregar convalidación\nError:"+e1.getErrorCode());
                    }
                }
            }
        });
        eliminar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                JFrame f = new JFrame();
                JPanel jCB = new JPanel();
                JPanel botones = new JPanel();
                JComboBox<Object> materiasa = new JComboBox<>();
                JButton volver = new JButton("Volver");
                JButton eliminar = new JButton("eliminar");
                for(int i = 0; i<materiasConvalidadas.getRowCount(); i++){
                    materiasa.addItem(materiasConvalidadas.getValueAt(i, 0)+"-"+materiasConvalidadas.getValueAt(i, 2));
                }
                f.setSize(852,225);
                f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                f.add(jCB);
                f.add(botones,BorderLayout.SOUTH);
                jCB.add(materiasa);
                botones.add(eliminar,BorderLayout.LINE_END);
                botones.add(volver,BorderLayout.LINE_END);
                f.setVisible(true);
                volver.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        f.setVisible(false);
                        setVisible(true);
                    }
                    
                }); 
                eliminar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String numMA = sql.printConsulta("Select distinct m.no_materia from materias  m, Convalidaciones c where m.nombre = '"+((String)materiasa.getSelectedItem()).split("-")[0]+"' and m.no_materia = c.clave_materia_cursada and c.no_control =  '"+no_control+"'");
                            numMA = numMA.trim();
                            String numMC = sql.printConsulta("Select distinct m.no_materia from materias  m, Convalidaciones c where m.nombre = '"+((String)materiasa.getSelectedItem()).split("-")[1]+"' and m.no_materia = c.clave_materia_convalidar and c.no_control =  '"+no_control+"' and m.carrera='"+carreraConvalidar.getSelectedItem()+"' and c.clave_materia_cursada ='"+numMA+"'");
                            numMC = numMC.trim();
                            sql.execute("delete from Convalidaciones where clave_materia_convalidar = '"+numMC+"' and clave_materia_cursada = '"+numMA+"' and no_control = '"+no_control+"'");
                            System.out.println("delete from Convalidaciones where clave_materia_convalidar = '"+numMC+"' and clave_materia_cursada = '"+numMA+"' and no_control = '"+no_control+"'");
                            materiasa.removeItemAt(materiasa.getSelectedIndex());
                            inicializar();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    
    public void inicializar() throws SQLException {
        sql.consultaJCB("select distinct carrera from materias where carrera <> (SELECT carrera FROM alumno WHERE no_control = '" + no_control + "')order by carrera asc", carreraConvalidar);
        sql.consultaJCB("select nombre from materias where no_materia in (select distinct clave_materia from Cursa where numero_control = '"+no_control+"') order by nombre asc", materiasAlumno);
        tableModelConvalidacion = sql.generarTabla("Select DISTINCT a.nombre AS 'Asignatura Cursada', cu.calificacion AS 'Calificación', b.nombre AS 'Asignatura Convalidada', b.clave_materia AS 'Clave', b.creditos AS 'Créditos' from materias a, materias b, cursa cu, Convalidaciones c where a.no_materia = c.clave_materia_cursada and b.no_materia = c.clave_materia_convalidar and c.no_control = '"+no_control+"'and cu.clave_materia = c.clave_materia_cursada");
        tableModelInfo = sql.generarTabla("SELECT Distinct no_control as 'No.Control', nombre as 'Nombre', apellidoP as 'Apellido Paterno', apellidoM as 'Apellido Materno', semestre as 'Semestre',carrera as 'Carrera de Procedencia' FROM ALUMNO WHERE no_control = '"+no_control+"'");
        materiasConvalidadas.setModel(tableModelConvalidacion);
        infoAlumno.setModel(tableModelInfo);
    
        // Calcular la altura necesaria para mostrar todas las filas
        int rowCount = tableModelInfo.getRowCount();
        int rowHeight = infoAlumno.getRowHeight();
        int preferredHeight = rowCount * rowHeight;

        // Establecer la altura preferida de la tabla infoAlumno
        infoAlumno.setPreferredScrollableViewportSize(new Dimension(579, preferredHeight));

        // Actualizar el diseño del JFrame para que se ajuste a los nuevos tamaños de los componentes
        revalidate();
        repaint();
    }
    
    public void generarPDF(JTable tableA, int[] celdasA, JTable tableB, int[] celdasB) throws IOException { //Método de generación del PDF
        PDDocument document = new PDDocument();
        PDPage firstPage = new PDPage(PDRectangle.LEGAL);
        document.addPage(firstPage);
    
        PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);
    
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.setLineWidth(1);
    
        int initX = 20;
        int initY = (int) firstPage.getMediaBox().getHeight() - 50;
    
        int cellHeight = 20;
    
        int rowCountA = tableA.getRowCount();
        int colCountA = tableA.getColumnCount();
        int rowCountB = tableB.getRowCount();
        int colCountB = tableB.getColumnCount();
    
        // Obtener el modelo de la tabla
        TableModel tableModel = tableA.getModel();
        TableModel tableModelB = tableB.getModel();
        //TABLA A
        // Obtener el ancho total de las celdas
        int totalCellWidth = 0;
        for (int i = 0; i < colCountA; i++) {
            totalCellWidth += celdasA[i];
        }
    
        // Imprimir encabezados de columnas
        for (int i = 0; i < colCountA; i++) {
            String columnName = tableModel.getColumnName(i);
            int cellWidth = celdasA[i]; // Obtener el ancho de la celda
            contentStream.addRect(initX, initY, cellWidth, -cellHeight);
            contentStream.beginText();
            contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.showText(columnName);
            contentStream.endText();
    
            initX += cellWidth;
        }
        
        
        initX = 20;
        initY -= cellHeight;
        
        // Imprimir datos de la tabla
        for (int i = 0; i < rowCountA; i++) {
            for (int j = 0; j < colCountA; j++) {
                Object value = tableModel.getValueAt(i, j);
                String cellData = (value != null) ? value.toString() : "";
    
                int cellWidth = celdasA[j]; // Obtener el ancho de la celda
                contentStream.addRect(initX, initY, cellWidth, -cellHeight);
                contentStream.beginText();
                contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.showText(cellData);
                contentStream.endText();
                
                initX += cellWidth;
            }
    
            initX = 5;
            initY -= cellHeight;
        }
        //TABLA B
        cellHeight = 15;
        initX = 20;
        initY -= cellHeight+10;

       // Obtener el ancho total de las celdas
       int totalCellWidthB = 0;
       for (int i = 0; i < colCountB; i++) {
           totalCellWidthB += celdasB[i];
       }
   
       // Imprimir encabezados de columnas
       for (int i = 0; i < colCountB; i++) {
           String columnName = tableModelB.getColumnName(i);
           int cellWidth = celdasB[i]; // Obtener el ancho de la celda
           contentStream.addRect(initX, initY, cellWidth, -cellHeight);
           contentStream.beginText();
           contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 3);
           contentStream.setFont(PDType1Font.HELVETICA, 8);
           contentStream.showText(columnName);
           contentStream.endText();
   
           initX += cellWidth;
       }
       
       
       initX = 20;
       initY -= cellHeight;
       
       // Imprimir datos de la tabla
       for (int i = 0; i < rowCountB; i++) {
           for (int j = 0; j < colCountB; j++) {
               Object value = tableModelB.getValueAt(i, j);
               String cellData = (value != null) ? value.toString() : "";
   
               int cellWidth = celdasB[j]; // Obtener el ancho de la celda
               contentStream.addRect(initX, initY, cellWidth, -cellHeight);
               contentStream.beginText();
               contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 3);
               contentStream.setFont(PDType1Font.HELVETICA, 8);
               contentStream.showText(cellData);
               contentStream.endText();
               
               initX += cellWidth;
           }
   
           initX = 20;
           initY -= cellHeight;
       }
    
        contentStream.stroke();
        contentStream.close();
    
        document.save(System.getProperty("user.dir") + "\\Convalidaciones\\Convalidación_"+no_control+".pdf");
        document.close();
    }    
    

    public void mostrar(Boolean b){
        setVisible(b);
        repaint();
    }
} 